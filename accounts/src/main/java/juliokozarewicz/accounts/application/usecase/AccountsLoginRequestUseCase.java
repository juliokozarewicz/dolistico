package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsLoginRequestCommand;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakUpdateUser;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AccountsLoginRequestUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final CacheManager cacheManager;
    private final Cache tokenVerificationCache;
    private final AccountsKeycloakUpdateUser accountsKeycloakUpdateUser;

    public AccountsLoginRequestUseCase(

        CacheManager cacheManager,
        AccountsKeycloakUpdateUser accountsKeycloakUpdateUser

    ) {

        this.cacheManager = cacheManager;
        this.accountsKeycloakUpdateUser = accountsKeycloakUpdateUser;
        this.tokenVerificationCache = cacheManager.getCache("accounts.tokenVerificationCache");

    }

    // ===================================================== ( constructor end )

    public void execute(

        Locale locale,
        AccountsLoginRequestCommand accountsLoginRequestCommand

    ) {

        // Password cleanup
        char[] password = accountsLoginRequestCommand.userPassword();

        try {

            // ##### Get user from Keycloak
            // ##### Null user verification
            // ##### Account banned (send email to user)
            // ##### Email not verified (send email to user)
            // ##### Create user login request token
            // ##### Create user pin (send email to user)

        }

        // Cleanup password
        finally {

            if (password != null) {
                java.util.Arrays.fill(password, '\0');
            }

        }

    }

}