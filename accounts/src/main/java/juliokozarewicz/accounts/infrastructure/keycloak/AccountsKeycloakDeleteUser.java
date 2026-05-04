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
    private final RestTemplate restTemplate;

    // Cache name
    private static final String cacheKey = "storedToken";

    public AccountsKeycloakDeleteUser(

        RestTemplate restTemplate

    ) {

        this.restTemplate = restTemplate;

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
            ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                request,
                Void.class
            );

            // Validate response
            if (!response.getStatusCode().is2xxSuccessful()) {

                logger.error("Error deleting user in Keycloak. Status: " + response.getStatusCode());
                throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

            }

        } catch (Exception e) {

            logger.error("Error accessing Keycloak [ AccountsKeycloakDeleteUser.execute() ]: " + e);
            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}