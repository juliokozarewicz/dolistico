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
import java.util.Locale;
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
    private final WebClient webClient;
    private final AccountsKeycloakClientTokenProvider accountsKeycloakClientTokenProvider;

    public AccountsKeycloakCreateUser(

        WebClient webClient,
        AccountsKeycloakClientTokenProvider accountsKeycloakClientTokenProvider

    ) {

        this.webClient = webClient;
        this.accountsKeycloakClientTokenProvider = accountsKeycloakClientTokenProvider;

    }

    // ===================================================== ( constructor end )

    public String execute(

        String userEmail,
        char[] userPassword

    ) {

        // Login client Keycloak
        String clientToken = accountsKeycloakClientTokenProvider.getAccessToken();

        // Build Keycloak admin user creation endpoint
        String url = keycloakBaseURL + "/admin/realms/" + keycloakRealm + "/users";

        // Request payload for Keycloak user creation
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("username", userEmail.toLowerCase(Locale.ROOT));
        body.put("email", userEmail.toLowerCase(Locale.ROOT));
        body.put("enabled", true);
        body.put("emailVerified", false);
        body.put("firstName", "");
        body.put("lastName", "");
        body.put("requiredActions", List.of());

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

            // Logs
            logger.atError()
            .addKeyValue("realm", keycloakRealm)
            .log("Error in the Keycloak response, user ID was not returned: [ AccountsKeycloakCreateUser.execute() ]");

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);
        }

        // Extract user ID from Location header URL
        URI location = response.getHeaders().getLocation();

        // Return user id created
        return location.getPath().substring(
            location.getPath().lastIndexOf("/") + 1
        );

    }

}