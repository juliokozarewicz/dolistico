package juliokozarewicz.accounts.infrastructure.keycloak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AccountsKeycloakTokenProvider {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${KEYCLOAK_PORT}")
    private String keycloakPort;

    @Value("${KEYCLOAK_REALM}")
    private String keycloakRealm;

    @Value("${KEYCLOAK_CLIENT_ID}")
    private String keycloakClientId;

    @Value("${KEYCLOAK_CLIENT_SECRET}")
    private String keycloakClientSecret;
    // -------------------------------------------------------------------------

    private final RestTemplate restTemplate;
    private String accessToken;
    private long expiresAt;

    public AccountsKeycloakTokenProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ===================================================== ( constructor end )

    /**
     * Returns a valid access token.
     *
     * If the cached token is still valid, it will be reused.
     * Otherwise, a new token is requested using client_credentials flow.
     */
    public synchronized String getAccessToken() {

        // 1. Return cached token if still valid
        if (accessToken != null && System.currentTimeMillis() < expiresAt) {
            return accessToken;
        }

        // 2. Build URL
        String url = "http://keycloak:8080/realms/" +
            keycloakRealm +
            "/protocol/openid-connect/token";

        // 3. Build request body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", keycloakClientId);
        body.add("client_secret", keycloakClientSecret);
        body.add("grant_type", "client_credentials");

        // 4. Build headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> request = new HttpEntity<>(body, headers);

        // 5. Call Keycloak
        Map<String, Object> response = restTemplate.postForObject(
                url,
                request,
                Map.class
        );

        // 6. Extract token
        this.accessToken = (String) response.get("access_token");

        // 7. Extract expiration safely
        Object expiresInObj = response.get("expires_in");
        long expiresIn = expiresInObj instanceof Number
                ? ((Number) expiresInObj).longValue()
                : Long.parseLong(expiresInObj.toString());

        this.expiresAt = System.currentTimeMillis() + (expiresIn * 1000L);

        // 8. Return token
        return accessToken;

    }

}