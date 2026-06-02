package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsCreateLogCommand;
import juliokozarewicz.accounts.application.command.AccountsUpdatePasswordCacheCommand;
import juliokozarewicz.accounts.application.command.AccountsUpdatePasswordCommand;
import juliokozarewicz.accounts.application.enums.AccountsUpdateEnum;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakUpdateUser;
import juliokozarewicz.accounts.infrastructure.messaging.enums.AccountsMessagingTopicEnum;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsEventProducer;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsUpdateEmailProducer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;

@Service
public class AccountsUpdatePasswordUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final CacheManager cacheManager;
    private final Cache tokenVerificationCache;
    private final AccountsUpdateEmailProducer accountsUpdateEmailProducer;
    private final AccountsKeycloakUpdateUser accountsKeycloakUpdateUser;
    private final AccountsEventProducer accountsEventProducer;

    public AccountsUpdatePasswordUseCase(

        CacheManager cacheManager,
        AccountsUpdateEmailProducer accountsUpdateEmailProducer,
        AccountsKeycloakUpdateUser accountsKeycloakUpdateUser,
        AccountsEventProducer accountsEventProducer

    ) {

        this.cacheManager = cacheManager;
        this.accountsUpdateEmailProducer = accountsUpdateEmailProducer;
        this.accountsKeycloakUpdateUser = accountsKeycloakUpdateUser;
        this.accountsEventProducer = accountsEventProducer;
        this.tokenVerificationCache = cacheManager.getCache("accounts.tokenVerificationCache");

    }

    // ===================================================== ( constructor end )

    public void execute(

        String userIp,
        String userAgent,
        Locale locale,
        AccountsUpdatePasswordCommand accountsUpdatePasswordCommand

    ) {

        // Password cleanup
        char[] newPassword = accountsUpdatePasswordCommand.userPassword();

        try {

            // Retrieve stored token cache
            var cachedToken = tokenVerificationCache.get(accountsUpdatePasswordCommand.token());

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

            // Update password via Keycloak
            var result = accountsKeycloakUpdateUser.updatePassword(
                cachedData.idUser(),
                accountsUpdatePasswordCommand.newPassword()
            );

            // Clean token cache
            tokenVerificationCache.evict(accountsUpdatePasswordCommand.token());

            // Update verify email
            accountsKeycloakUpdateUser.updateVerifyEmail( cachedData.idUser() );

            // Send email notification
            accountsUpdateEmailProducer.execute(
                locale,
                result.get("email").toString()
            );

            // Create user account log
            AccountsCreateLogCommand logData = new AccountsCreateLogCommand(
                cachedData.idUser(),
                userIp,
                userAgent,
                AccountsUpdateEnum.ACCOUNTS_UPDATE_PASSWORD.getReasonCode(),
                ZonedDateTime.now(ZoneOffset.UTC).toInstant(),
                null,
                null
            );

            accountsEventProducer.accountLogProducer(logData);

        }

        // Cleanup password
        finally {

            if (newPassword != null) {
                java.util.Arrays.fill(newPassword, '\0');
            }

        }

    }

}