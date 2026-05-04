package juliokozarewicz.accounts.infrastructure.keycloak;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsKeycloakCreateUser.class);
    private final RestTemplate restTemplate;
    private static final String cacheKey = "storedToken";
    private final AccountsKeycloakDeleteUser accountsKeycloakDeleteUser;

    public AccountsKeycloakCreateUser(

        RestTemplate restTemplate,
        AccountsKeycloakDeleteUser accountsKeycloakDeleteUser

    ) {

        this.restTemplate = restTemplate;
        this.accountsKeycloakDeleteUser = accountsKeycloakDeleteUser;

    }

    // ===================================================== ( constructor end )

    public String execute (

        String clientToken,
        String userEmail,
        String userPassword

    ) {

        try {

            // Build URL
            String url = "http://keycloak:8080/admin/realms/" + keycloakRealm + "/users";

            // Build request body
            Map<String, Object> createUserBody = new LinkedHashMap<>();
            createUserBody.put("username", userEmail);
            createUserBody.put("email", userEmail);
            createUserBody.put("enabled", true);
            createUserBody.put("emailVerified", false);

            // Password definition
            Map<String, Object> credential = new LinkedHashMap<>();
            credential.put("type", "password");
            credential.put("value", userPassword);
            credential.put("temporary", false);

            // Add password map to createUserBody
            createUserBody.put("credentials", List.of(credential));

            // Build headers
             HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(clientToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(createUserBody, headers);

            // Call Keycloak
            ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                Void.class
            );

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

        } catch (Exception e) {

            logger.error("Error accessing Keycloak [ AccountsKeycloakCreateUser.execute() ]: " + e);
            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}