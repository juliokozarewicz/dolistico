package juliokozarewicz.accounts.infrastructure.worker;

import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetInactiveUsers;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AccountsDailyWorker {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsKeycloakGetInactiveUsers accountsKeycloakGetInactiveUsers;

    public AccountsDailyWorker(

        AccountsKeycloakGetInactiveUsers accountsKeycloakGetInactiveUsers

    ) {

        this.accountsKeycloakGetInactiveUsers = accountsKeycloakGetInactiveUsers;

    }

    // ===================================================== ( constructor end )

    @Scheduled(cron = "0 40 10 * * *", zone = "America/Sao_Paulo")
    public void deleteAccountExpiredJob() {

        // Get and delete expired users
        accountsKeycloakGetInactiveUsers.getInactiveUsers();

    }

}