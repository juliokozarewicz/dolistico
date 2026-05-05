package juliokozarewicz.accounts.infrastructure.keycloak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountsKeycloakCreateUser {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${ACCOUNTS_KEYCLOAK_REALM}")
    private String keycloakRealm;

    @Value("${KEYCLOAK_BASE_URL}")
    private String keycloakBaseURL;
    // -------------------------------------------------------------------------

    private static final String cacheKey = "storedToken";
    private final WebClient webClient;

    public AccountsKeycloakCreateUser(

        WebClient webClient

    ) {

        this.webClient = webClient;

    }

    // ===================================================== ( constructor end )

    public String execute(

        String clientToken,
        String userEmail,
        char[] userPassword

    ) {

        try {

            // Build Keycloak admin user creation endpoint
            String url = keycloakBaseURL + "/admin/realms/" + keycloakRealm + "/users";

            // Request payload for Keycloak user creation
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("username", userEmail);
            body.put("email", userEmail);
            body.put("enabled", true);
            body.put("emailVerified", false);

            // Credential (password) configuration
            Map<String, Object> credential = new LinkedHashMap<>();
            credential.put("type", "password");
            credential.put("value", new String(userPassword));
            credential.put("temporary", false);
            body.put("credentials", List.of(credential));

            // Execute HTTP POST request to Keycloak Admin API
            ResponseEntity<Void> response = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block();

            // Validate response contains Location header (user ID reference)
            if (response == null || response.getHeaders().getLocation() == null) {
                throw new IllegalStateException("Keycloak did not return user location");
            }

            // Extract user ID from Location header URL
            URI location = response.getHeaders().getLocation();

            // Return user id created
            return location.getPath().substring(
                location.getPath().lastIndexOf("/") + 1
            );

        } finally {

            // Cleanup password
            if (userPassword != null) {
                java.util.Arrays.fill(userPassword, '\0');
            }

        }

    }

}