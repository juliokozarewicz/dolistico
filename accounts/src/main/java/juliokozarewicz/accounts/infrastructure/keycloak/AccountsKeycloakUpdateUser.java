package juliokozarewicz.accounts.infrastructure.keycloak;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class AccountsKeycloakUpdateUser {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------

    @Value("${ACCOUNTS_KEYCLOAK_REALM}")
    private String keycloakRealm;

    @Value("${KEYCLOAK_BASE_URL}")
    private String keycloakBaseURL;

    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsKeycloakUpdateUser.class);
    private final WebClient webClient;
    private final AccountsKeycloakClientTokenProvider accountsKeycloakClientTokenProvider;

    public AccountsKeycloakUpdateUser(

        WebClient webClient,
        AccountsKeycloakClientTokenProvider accountsKeycloakClientTokenProvider

    ) {

        this.webClient = webClient;
        this.accountsKeycloakClientTokenProvider = accountsKeycloakClientTokenProvider;

    }

    // ===================================================== ( constructor end )

    // ======================================================== ( helpers init )

    private Map<String, Object> getUserById(

        String clientToken,
        String idUser
    ) {

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

    // Updates user password in Keycloak and returns id and email
    public LinkedHashMap<String, Object> updatePassword(

        String idUser,
        String password

    ) {

        try {

            // Retrieves access token for authentication
            String clientToken = accountsKeycloakClientTokenProvider.getAccessToken();

            // Keycloak endpoint for password reset
            String url = keycloakBaseURL
                + "/admin/realms/"
                + keycloakRealm
                + "/users/"
                + idUser
                + "/reset-password";

            // Request body required by Keycloak password reset endpoint
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("type", "password");
            body.put("value", password);
            body.put("temporary", false);

            // Executes password update request
            ResponseEntity<Void> response = webClient.put()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block();

            // Validates response success
            if (response == null || !response.getStatusCode().is2xxSuccessful()) {
                logger.atError()
                .addKeyValue("realm", keycloakRealm)
                .log("Error updating password in Keycloak [ AccountsKeycloakUpdateUser.updatePassword() ]");

                throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);
            }

            // Retrieves updated user data
            Map<String, Object> user = getUserById(clientToken, idUser);
            String email = user != null ? (String) user.get("email") : null;

            // Builds response payload
            LinkedHashMap<String, Object> result = new LinkedHashMap<>();
            result.put("id", idUser);
            result.put("email", email);

            return result;

        } catch (Exception e) {

            // Logs unexpected errors during password update
            logger.atError()
            .addKeyValue("realm", keycloakRealm)
            .log("Exception updating password in Keycloak [ AccountsKeycloakUpdateUser.updatePassword() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

    // Updates user email in Keycloak and returns id and previous email
    public LinkedHashMap<String, Object> updateEmail(

        String idUser,
        String newEmail

    ) {

        try {

            // Retrieves access token for authentication
            String clientToken = accountsKeycloakClientTokenProvider.getAccessToken();

            // Retrieves current user data before update
            Map<String, Object> user = getUserById(clientToken, idUser);
            String oldEmail = user != null ? (String) user.get("email") : null;

            // Keycloak endpoint for user update
            String url = keycloakBaseURL
                + "/admin/realms/"
                + keycloakRealm
                + "/users/"
                + idUser;

            // Request body for email update
            Map<String, Object> body = new LinkedHashMap<>(user);
            body.put("email", newEmail.toLowerCase(Locale.ROOT));
            body.put("username", newEmail.toLowerCase(Locale.ROOT));

            // Executes email update request
            ResponseEntity<Void> response = webClient.put()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block();

            // Validates response success
            if (response == null || !response.getStatusCode().is2xxSuccessful()) {

                logger.atError()
                .addKeyValue("realm", keycloakRealm)
                .log("Error updating email in Keycloak [ AccountsKeycloakUpdateUser.updateEmail() ]");

                throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

            }

            // Builds response payload containing previous email
            LinkedHashMap<String, Object> result = new LinkedHashMap<>();
            result.put("id", idUser);
            result.put("oldEmail", oldEmail);

            return result;

        } catch (Exception e) {

            // Logs unexpected errors during email update
            logger.atError()
            .addKeyValue("realm", keycloakRealm)
            .log("Exception updating email in Keycloak [ AccountsKeycloakUpdateUser.updateEmail() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

    // Updates user emailVerified flag in Keycloak and returns id and email
    public void updateVerifyEmail(

        String idUser

    ) {

        try {

            // Retrieves access token for authentication
            String clientToken = accountsKeycloakClientTokenProvider.getAccessToken();

            // Retrieves current user data
            Map<String, Object> user = getUserById(clientToken, idUser);

            String url = keycloakBaseURL
                + "/admin/realms/"
                + keycloakRealm
                + "/users/"
                + idUser;

            // Request body with full object merge to avoid overwriting fields
            Map<String, Object> body = new LinkedHashMap<>(user);
            body.put("emailVerified", true);

            ResponseEntity<Void> response = webClient.put()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block();

            if (response == null || !response.getStatusCode().is2xxSuccessful()) {
                logger.atError()
                .addKeyValue("realm", keycloakRealm)
                .log("Error updating emailVerified in Keycloak [ AccountsKeycloakUpdateUser.updateVerifyEmail() ]");

                throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);
            }

        } catch (Exception e) {

            // Logs unexpected errors during emailVerified update
            logger.atError()
            .addKeyValue("realm", keycloakRealm)
            .log("Exception updating emailVerified in Keycloak [ AccountsKeycloakUpdateUser.updateVerifyEmail() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

    // Bans user account in Keycloak by setting enabled to false
    public void banUser(

        String idUser

    ) {

        try {

            // Retrieves access token for authentication
            String clientToken = accountsKeycloakClientTokenProvider.getAccessToken();

            // Retrieves current user data
            Map<String, Object> user = getUserById(clientToken, idUser);

            // Keycloak endpoint for user update
            String url = keycloakBaseURL
                + "/admin/realms/"
                + keycloakRealm
                + "/users/"
                + idUser;

            // Request body with full object merge to avoid overwriting fields
            Map<String, Object> body = new LinkedHashMap<>(user);
            body.put("enabled", false);

            // Executes ban request
            ResponseEntity<Void> response = webClient.put()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block();

            // Validates response success
            if (response == null || !response.getStatusCode().is2xxSuccessful()) {

                logger.atError()
                .addKeyValue("realm", keycloakRealm)
                .log("Error banning user in Keycloak [ AccountsKeycloakUpdateUser.banUser() ]");

                throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

            }

        } catch (Exception e) {

            // Logs unexpected errors during ban
            logger.atError()
            .addKeyValue("realm", keycloakRealm)
            .log("Exception banning user in Keycloak [ AccountsKeycloakUpdateUser.banUser() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}