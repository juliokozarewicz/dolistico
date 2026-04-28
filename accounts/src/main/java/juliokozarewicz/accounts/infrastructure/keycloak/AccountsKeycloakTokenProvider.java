package juliokozarewicz.accounts.infrastructure.keycloak;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AccountsKeycloakTokenProvider {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${KEYCLOAK_PORT}")
    private String keycloakPort;

    @Value("${ACCOUNTS_KEYCLOAK_REALM}")
    private String keycloakRealm;

    @Value("${ACCOUNTS_KEYCLOAK_CLIENT_ID}")
    private String keycloakClientId;

    @Value("${ACCOUNTS_KEYCLOAK_CLIENT_SECRET}")
    private String keycloakClientSecret;
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsKeycloakTokenProvider.class);
    private final RestTemplate restTemplate;
    private final CacheManager cacheManager;
    private final Cache clientKeycloakTokenCache;

    // Cache name
    private static final String cacheKey = "storedToken";

    public AccountsKeycloakTokenProvider(

        RestTemplate restTemplate,
        CacheManager cacheManager

    ) {

        this.restTemplate = restTemplate;
        this.cacheManager = cacheManager;
        this.clientKeycloakTokenCache = cacheManager.getCache("accounts.clientKeycloakTokenCache");

    }

    // ===================================================== ( constructor end )
    /**
     * Returns a valid access token.
     *
     * Strategy:
     * 1. Try Redis cache
     * 2. If not present, fetch new token
     * 3. Store with dynamic TTL (based on expires_in - safety margin)
     */
    public synchronized String getAccessToken() {

        try {

            // Try Redis
            Cache.ValueWrapper cachedToken = clientKeycloakTokenCache.get(cacheKey);

            if (cachedToken != null) {
                return (String) ((LinkedHashMap<?, ?>) cachedToken.get()).get("token");
            }

            // Build URL
            String url = "http://keycloak:8080/realms/" +
                keycloakRealm +
                "/protocol/openid-connect/token";

            // Build request body
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", keycloakClientId);
            body.add("client_secret", keycloakClientSecret);
            body.add("grant_type", "client_credentials");

            // Build headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<?> request = new HttpEntity<>(body, headers);

            // Call Keycloak
            Map<String, Object> response = restTemplate.postForObject(
                url,
                request,
                Map.class
            );

            // Extract
            String token = (String) response.get("access_token");

            // Map
            LinkedHashMap<String, String> tokenMap = new LinkedHashMap<>();
            tokenMap.put("token", token);

            // Store
            clientKeycloakTokenCache.put(cacheKey, tokenMap);

            // Return token
            return token;

        } catch (Exception e) {

            logger.error("Error accessing Keycloak [ AccountsKeycloakTokenProvider.getAccessToken() ]: " + e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}