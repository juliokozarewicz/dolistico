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
    private final AccountsKeycloakClientTokenProvider accountsKeycloakClientTokenProvider;

    public AccountsKeycloakGetUser(

        AccountsKeycloakClientTokenProvider accountsKeycloakClientTokenProvider,
        WebClient webClient

    ) {

        this.accountsKeycloakClientTokenProvider = accountsKeycloakClientTokenProvider;
        this.webClient = webClient;

    }

    // ===================================================== ( constructor end )

    // ======================================================== ( helpers init )

    private Map<String, Object> getUserById(String clientToken, String idUser) {

        URI uri = UriComponentsBuilder
            .fromUriString(keycloakBaseURL)
            .pathSegment("admin", "realms", keycloakRealm, "users", idUser)
            .encode()
            .build()
            .toUri();

        return webClient.get()
        .uri(uri)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + clientToken)
        .retrieve()
        .bodyToMono(Map.class)
        .block();

    }

    // ========================================================= ( helpers end )

    // Get user id by email
    public String getUserByEmail( String userEmail ) {

        try {

            String clientToken = accountsKeycloakClientTokenProvider.getAccessToken();

            URI uri = UriComponentsBuilder
                .fromUriString(keycloakBaseURL)
                .pathSegment("admin", "realms", keycloakRealm, "users")
                .queryParam("email", userEmail)
                .queryParam("exact", true)
                .encode()
                .build()
                .toUri();

            List<Map<String, Object>> users = webClient.get()
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

            // Logs
            logger.atError()
            .addKeyValue("realm", keycloakRealm)
            .log("Error getting user by email in Keycloak [ AccountsKeycloakGetUser.getUserByEmail() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

    // Check if user email is verified by user ID
    public boolean isAccountVerifiedById(String idUser) {

        try {

            String clientToken = accountsKeycloakClientTokenProvider.getAccessToken();

            Map<String, Object> user = getUserById(clientToken, idUser);

            if (user == null || user.isEmpty()) {
                return false;
            }

            return Boolean.TRUE.equals(user.get("emailVerified"));

        } catch (Exception e) {

            // Logs
            logger.atError()
            .addKeyValue("realm", keycloakRealm)
            .log("Error checking account email verification in Keycloak [ AccountsKeycloakGetUser.isAccountVerifiedById() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

    // Check if user account is enabled (active) by user ID
    public boolean isAccountEnabledById(String idUser) {

        try {

            String clientToken = accountsKeycloakClientTokenProvider.getAccessToken();

            Map<String, Object> user = getUserById(clientToken, idUser);

            if (user == null || user.isEmpty()) {
                return false;
            }

            return Boolean.TRUE.equals(user.get("enabled"));

        } catch (Exception e) {

            // Logs
            logger.atError()
            .addKeyValue("realm", keycloakRealm)
            .log("Error checking account enabled status in Keycloak [ AccountsKeycloakGetUser.isAccountEnabledById() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}