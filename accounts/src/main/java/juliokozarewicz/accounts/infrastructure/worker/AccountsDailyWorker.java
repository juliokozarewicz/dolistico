package juliokozarewicz.accounts.infrastructure.worker;

import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakClientTokenProvider;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetInactiveUsers;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AccountsDailyWorker {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsKeycloakClientTokenProvider accountsKeycloakClientTokenProvider;
    private final AccountsKeycloakGetInactiveUsers accountsKeycloakGetInactiveUsers;

    public AccountsDailyWorker(

        AccountsKeycloakClientTokenProvider accountsKeycloakClientTokenProvider,
        AccountsKeycloakGetInactiveUsers accountsKeycloakGetInactiveUsers

    ) {

        this.accountsKeycloakClientTokenProvider = accountsKeycloakClientTokenProvider;
        this.accountsKeycloakGetInactiveUsers = accountsKeycloakGetInactiveUsers;

    }

    // ===================================================== ( constructor end )

    @Scheduled(cron = "0 29 18 * * *", zone = "America/Sao_Paulo")
    public void deleteAccountExpiredJob() {

        // Login client Keycloak
        String clientToken = accountsKeycloakClientTokenProvider.getAccessToken();

        // Get and delete expired users
        accountsKeycloakGetInactiveUsers.getInactiveUsers(clientToken);

    }

}