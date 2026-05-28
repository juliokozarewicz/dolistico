package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsLoginRequestCommand;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUser;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakUpdateUser;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsUserBannedProducer;
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
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;
    private final AccountsUserBannedProducer accountsUserBannedProducer;

    public AccountsLoginRequestUseCase(

        CacheManager cacheManager,
        AccountsKeycloakUpdateUser accountsKeycloakUpdateUser,
        AccountsKeycloakGetUser accountsKeycloakGetUser,
        AccountsUserBannedProducer accountsUserBannedProducer

    ) {

        this.cacheManager = cacheManager;
        this.accountsKeycloakUpdateUser = accountsKeycloakUpdateUser;
        this.tokenVerificationCache = cacheManager.getCache("accounts.tokenVerificationCache");
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;
        this.accountsUserBannedProducer = accountsUserBannedProducer;

    }

    // ===================================================== ( constructor end )

    public void execute(

        Locale locale,
        AccountsLoginRequestCommand accountsLoginRequestCommand

    ) {

        // Password cleanup
        char[] password = accountsLoginRequestCommand.userPassword();

        try {

            // Get user by email from Keycloak
            String existingUserId = accountsKeycloakGetUser.getUserByEmail(
                accountsLoginRequestCommand.email()
            );

            // Null user verification
            if (existingUserId == null) {
                throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
            }

            // Account banned (send email to user)
            if (accountsKeycloakGetUser.isAccountBannedById(existingUserId) ) {
                accountsUserBannedProducer.execute(locale, accountsLoginRequestCommand.email());
                throw new DomainException(DomainExceptionEnum.NO_PERMISSION_TO_ACCESS);
            }

            // ##### Create user login request token
            // ##### Create user pin
            // ##### Send email to user with url (token + pin)

        }

        // Cleanup password
        finally {

            if (password != null) {
                java.util.Arrays.fill(password, '\0');
            }

        }

    }

}