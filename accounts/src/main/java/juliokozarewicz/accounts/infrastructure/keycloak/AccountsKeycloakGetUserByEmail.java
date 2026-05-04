package juliokozarewicz.accounts.infrastructure.keycloak;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountsKeycloakGetUserByEmail {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${ACCOUNTS_KEYCLOAK_REALM}")
    private String keycloakRealm;

    @Value("${KC_BASE_URL}")
    private String keycloakBaseURL;
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsKeycloakGetUserByEmail.class);
    private final RestTemplate restTemplate;

    // Cache name
    private static final String cacheKey = "storedToken";

    public AccountsKeycloakGetUserByEmail(

        RestTemplate restTemplate

    ) {

        this.restTemplate = restTemplate;

    }

    // ===================================================== ( constructor end )

    public String execute ( String clientToken, String userEmail ) {

        try {

            // Build Keycloak search URL
            String url = keycloakBaseURL + "/admin/realms/" + keycloakRealm + "/users?email=" + userEmail;

            // Build headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(clientToken);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            // Call Keycloak (returns list of users)
            ResponseEntity<List> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                List.class
            );

            List<?> users = response.getBody();

            // If no users found, return null
            if (users == null || users.isEmpty()) { return null; }

            // Extract first user (Keycloak returns list of maps)
            Map<String, Object> user =
                (Map<String, Object>) users.get(0);

            // Return userId
            return (String) user.get("id");

        } catch (Exception e) {

            logger.error("Error getting user by email in Keycloak", e);
            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}