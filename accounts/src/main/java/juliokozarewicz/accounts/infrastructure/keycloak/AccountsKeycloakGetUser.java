package juliokozarewicz.accounts.infrastructure.keycloak;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class AccountsKeycloakGetUser {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${ACCOUNTS_KEYCLOAK_REALM}")
    private String keycloakRealm;

    @Value("${KEYCLOAK_BASE_URL}")
    private String keycloakBaseURL;
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsKeycloakGetUser.class);
    private final WebClient webClient;

    public AccountsKeycloakGetUser(

        WebClient webClient

    ) {

        this.webClient = webClient;

    }

    // ===================================================== ( constructor end )

    public String getUserByEmail(String clientToken, String userEmail ) {

        try {

            // Build Keycloak search URL
            URI uri = UriComponentsBuilder
                .fromUriString(keycloakBaseURL)
                .pathSegment("admin", "realms", keycloakRealm, "users")
                .queryParam("email", userEmail)
                .queryParam("exact", true)
                .encode()
                .build()
                .toUri();

            // Get user
            List<?> users = webClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + clientToken)
                .retrieve()
                .bodyToMono(List.class)
                .block();

            // If no users found, return null
            if (users == null || users.isEmpty()) { return null; }

            // Extract first user (Keycloak returns list of maps)
            Map<String, Object> user = (Map<String, Object>) users.get(0);

            // Return userId
            return (String) user.get("id");

        } catch (Exception e) {

            logger.error("Error getting user by email in Keycloak", e);
            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}