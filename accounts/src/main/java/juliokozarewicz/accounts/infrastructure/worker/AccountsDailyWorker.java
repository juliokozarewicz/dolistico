package juliokozarewicz.accounts.infrastructure.worker;

import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetInactiveUsers;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

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

    @Scheduled(cron = "0 0 6 * * *", zone = "UTC")
    public void deleteAccountExpiredJob() {

        // Get and delete expired users
        accountsKeycloakGetInactiveUsers.getInactiveUsers();

    }

}