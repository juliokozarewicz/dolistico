package juliokozarewicz.accounts.infrastructure.keycloak;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AccountsKeycloakDeleteUser {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${ACCOUNTS_KEYCLOAK_REALM}")
    private String keycloakRealm;

    @Value("${KEYCLOAK_BASE_URL}")
    private String keycloakBaseURL;
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsKeycloakDeleteUser.class);
    private final WebClient webClient;
    private static final String cacheKey = "storedToken";

    public AccountsKeycloakDeleteUser(

        WebClient webClient

    ) {

        this.webClient = webClient;

    }

    // ===================================================== ( constructor end )

    public void execute (

        String clientToken,
        String userId

    ) {

        try {

            // Build URL
            String url = keycloakBaseURL + "/admin/realms/" + keycloakRealm + "/users/" + userId;

            // Build headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(clientToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            // Call Keycloak
            ResponseEntity<Void> response = webClient.delete()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + clientToken)
                .retrieve()
                .toBodilessEntity()
                .block();

            // Validate response
            if (!response.getStatusCode().is2xxSuccessful()) {

                // Logs
                logger.atError()
                .addKeyValue("realm", keycloakRealm)
                .addKeyValue("statusCode", response.getStatusCode().value())
                .log("Error deleting user in Keycloak: [ AccountsKeycloakDeleteUser.execute() ]");

                throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

            }

        } catch (Exception e) {

            // Logs
            logger.atError()
            .addKeyValue("realm", keycloakRealm)
            .log("Error accessing Keycloak [ AccountsKeycloakDeleteUser.execute() ]", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}