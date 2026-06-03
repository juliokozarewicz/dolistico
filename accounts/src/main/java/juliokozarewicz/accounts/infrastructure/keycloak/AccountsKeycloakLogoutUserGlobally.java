package juliokozarewicz.accounts.infrastructure.keycloak;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AccountsKeycloakLogoutUserGlobally {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${ACCOUNTS_KEYCLOAK_REALM}")
    private String keycloakRealm;

    @Value("${KEYCLOAK_BASE_URL}")
    private String keycloakBaseURL;
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsKeycloakLogoutUserGlobally.class);
    private final WebClient webClient;
    private final AccountsKeycloakClientTokenProvider accountsKeycloakClientTokenProvider;

    public AccountsKeycloakLogoutUserGlobally(

        WebClient webClient,
        AccountsKeycloakClientTokenProvider accountsKeycloakClientTokenProvider

    ) {

        this.webClient = webClient;
        this.accountsKeycloakClientTokenProvider = accountsKeycloakClientTokenProvider;

    }

    // ===================================================== ( constructor end )

    public void execute(

        String idUser

    ) {

        try {

            // Admin token (client credentials)
            String clientToken = accountsKeycloakClientTokenProvider.getAccessToken();

            // Keycloak admin logout endpoint
            String url = keycloakBaseURL
                + "/admin/realms/"
                + keycloakRealm
                + "/users/"
                + idUser
                + "/logout";

            // Call Keycloak to kill ALL sessions
            webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity()
                .block();

        } catch (Exception e) {

            logger.atError()
            .addKeyValue("realm", keycloakRealm)
            .log("Error logging out user globally in Keycloak [ AccountsKeycloakLogoutUserGlobally.execute() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}