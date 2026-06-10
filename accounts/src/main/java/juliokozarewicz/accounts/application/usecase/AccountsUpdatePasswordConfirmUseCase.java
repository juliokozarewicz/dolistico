package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsCreateLogCommand;
import juliokozarewicz.accounts.application.command.AccountsUpdatePasswordCacheCommand;
import juliokozarewicz.accounts.application.command.AccountsUpdatePasswordConfirmCommand;
import juliokozarewicz.accounts.application.enums.AccountsUpdateEnum;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUser;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakLogoutUserGlobally;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakUpdateUser;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsEventProducer;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsUpdateEmailConfirmProducer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;

@Service
public class AccountsUpdatePasswordConfirmUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final CacheManager cacheManager;
    private final Cache tokenVerificationCache;
    private final AccountsUpdateEmailConfirmProducer accountsUpdateEmailConfirmProducer;
    private final AccountsKeycloakUpdateUser accountsKeycloakUpdateUser;
    private final AccountsEventProducer accountsEventProducer;
    private final AccountsKeycloakLogoutUserGlobally accountsKeycloakLogoutUserGlobally;
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;

    public AccountsUpdatePasswordConfirmUseCase(

        CacheManager cacheManager,
        AccountsUpdateEmailConfirmProducer accountsUpdateEmailConfirmProducer,
        AccountsKeycloakUpdateUser accountsKeycloakUpdateUser,
        AccountsEventProducer accountsEventProducer,
        AccountsKeycloakLogoutUserGlobally accountsKeycloakLogoutUserGlobally,
        AccountsKeycloakGetUser accountsKeycloakGetUser

    ) {

        this.cacheManager = cacheManager;
        this.accountsUpdateEmailConfirmProducer = accountsUpdateEmailConfirmProducer;
        this.accountsKeycloakUpdateUser = accountsKeycloakUpdateUser;
        this.accountsEventProducer = accountsEventProducer;
        this.accountsKeycloakLogoutUserGlobally = accountsKeycloakLogoutUserGlobally;
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;
        this.tokenVerificationCache = cacheManager.getCache("accounts.tokenVerificationCache");

    }

    // ===================================================== ( constructor end )

    public void execute(

        String userIp,
        String userAgent,
        Locale locale,
        AccountsUpdatePasswordConfirmCommand accountsUpdatePasswordConfirmCommand

    ) {

        // Password cleanup
        char[] newPassword = accountsUpdatePasswordConfirmCommand.userPassword();

        try {

            // Retrieve stored token cache
            var cachedToken = tokenVerificationCache.get(accountsUpdatePasswordConfirmCommand.token());

            // Null verification
            if (cachedToken == null) {
                throw new DomainException(DomainExceptionEnum.ACCOUNTS_EXPIRED_LINK);
            }

            // Reason verification
            AccountsUpdatePasswordCacheCommand cachedData = (AccountsUpdatePasswordCacheCommand) cachedToken.get();

            if (
                !AccountsUpdateEnum.ACCOUNTS_UPDATE_PASSWORD.getReasonCode()
                .equals(cachedData.reason())
            ) {
                throw new DomainException(DomainExceptionEnum.ACCOUNTS_EXPIRED_LINK);
            }

            // User ID extract
            String idUser = cachedData.idUser();

            // Account banned
            if ( accountsKeycloakGetUser.isAccountBannedById(idUser) ) {
                throw new DomainException(DomainExceptionEnum.NO_PERMISSION_TO_ACCESS);
            }

            // Update password via Keycloak
            var result = accountsKeycloakUpdateUser.updatePassword(
                idUser,
                accountsUpdatePasswordConfirmCommand.newPassword()
            );

            // Clean token cache
            tokenVerificationCache.evict(accountsUpdatePasswordConfirmCommand.token());

            // Update verify email
            accountsKeycloakUpdateUser.updateVerifyEmail(idUser);

            // Send email notification
            accountsUpdateEmailConfirmProducer.execute(
                locale,
                result.get("email").toString()
            );

            // Create user account log
            AccountsCreateLogCommand logData = new AccountsCreateLogCommand(
                idUser,
                userIp,
                userAgent,
                AccountsUpdateEnum.ACCOUNTS_UPDATE_PASSWORD.getReasonCode(),
                ZonedDateTime.now(ZoneOffset.UTC).toInstant(),
                null,
                null
            );

            accountsEventProducer.accountLogProducer(logData);

            // Revoke all user access
            accountsKeycloakLogoutUserGlobally.execute(idUser);

            // Revoke cache
            tokenVerificationCache.evict(accountsUpdatePasswordConfirmCommand.token());

        }

        // Cleanup password
        finally {

            if (newPassword != null) {
                java.util.Arrays.fill(newPassword, '\0');
            }

        }

    }

}