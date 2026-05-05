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

    private static final Logger logger = LoggerFactory.getLogger(AccountsKeycloakCreateUser.class);
    private static final String cacheKey = "storedToken";
    private final AccountsKeycloakDeleteUser accountsKeycloakDeleteUser;
    private final WebClient webClient;

    public AccountsKeycloakCreateUser(

        WebClient webClient,
        AccountsKeycloakDeleteUser accountsKeycloakDeleteUser

    ) {

        this.accountsKeycloakDeleteUser = accountsKeycloakDeleteUser;
        this.webClient = webClient;

    }

    // ===================================================== ( constructor end )

    public String execute (

        String clientToken,
        String userEmail,
        char[] userPassword

    ) {

        try {

            // Build URL
            String url = keycloakBaseURL + "/admin/realms/" + keycloakRealm + "/users";

            // Build request body
            Map<String, Object> createUserBody = new LinkedHashMap<>();
            createUserBody.put("username", userEmail);
            createUserBody.put("email", userEmail);
            createUserBody.put("enabled", true);
            createUserBody.put("emailVerified", false);

            // Password definition
            Map<String, Object> credential = new LinkedHashMap<>();
            credential.put("type", "password");
            credential.put("value", new String(userPassword));
            credential.put("temporary", false);

            // Add password map to createUserBody
            createUserBody.put("credentials", List.of(credential));

            // Build headers
            ResponseEntity<Void> response = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createUserBody)
                .retrieve()
                .toBodilessEntity()
                .block();

            // Extract userId from Location header
            URI location = response.getHeaders().getLocation();

            // ------------------------ ( delete user if getting id fails init )
            if ( location == null ) {
                accountsKeycloakDeleteUser.execute(clientToken, userEmail);
                logger.error("Error in the Keycloak response: User ID was not returned.");
                throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);
            }
            // ------------------------- ( delete user if getting id fails end )

            // ##### Producing a message for profile creation

            String path = location.getPath();
            String userId = path.substring(path.lastIndexOf("/") + 1);

            // Return user id
            return userId;

        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.Conflict e) {

            throw new DomainException(DomainExceptionEnum.ACCOUNTS_USER_ALREADY_EXISTS);

        } catch (Exception e) {

            logger.error("Error accessing Keycloak [ AccountsKeycloakCreateUser.execute() ]: " + e);
            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        } finally {

            // Clean password
            if (userPassword != null) {
                java.util.Arrays.fill(userPassword, '\0');
            }

        }

    }

}