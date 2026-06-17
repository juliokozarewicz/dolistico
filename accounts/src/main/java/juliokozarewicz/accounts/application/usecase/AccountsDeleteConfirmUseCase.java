package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.adapter.rest.dto.AccountsDeleteConfirmDTO;
import juliokozarewicz.accounts.adapter.rest.dto.AccountsUpdateEmailConfirmDTO;
import juliokozarewicz.accounts.application.command.AccountsCreateLogCommand;
import juliokozarewicz.accounts.application.command.AccountsUpdateEmailCacheCommand;
import juliokozarewicz.accounts.application.enums.AccountsUpdateEnum;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.keycloak.*;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsDeleteConfirmProducer;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsEventProducer;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsUpdateEmailProducer;
import juliokozarewicz.accounts.infrastructure.security.Encryption;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Map;

@Service
public class AccountsDeleteConfirmUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final CacheManager cacheManager;
    private final Cache tokenVerificationCache;
    private final AccountsEventProducer accountsEventProducer;
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;
    private final AccountsKeycloakDeleteUser accountsKeycloakDeleteUser;
    private final AccountsKeycloakLogin accountsKeycloakLogin;
    private final AccountsDeleteConfirmProducer accountsDeleteConfirmProducer;

    public AccountsDeleteConfirmUseCase(

        CacheManager cacheManager,
        AccountsEventProducer accountsEventProducer,
        AccountsKeycloakGetUser accountsKeycloakGetUser,
        AccountsKeycloakDeleteUser accountsKeycloakDeleteUser,
        AccountsKeycloakLogin accountsKeycloakLogin,
        AccountsDeleteConfirmProducer accountsDeleteConfirmProducer

    ) {

        this.cacheManager = cacheManager;
        this.tokenVerificationCache = cacheManager.getCache("accounts.tokenVerificationCache");
        this.accountsEventProducer = accountsEventProducer;
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;
        this.accountsKeycloakDeleteUser = accountsKeycloakDeleteUser;
        this.accountsKeycloakLogin = accountsKeycloakLogin;
        this.accountsDeleteConfirmProducer = accountsDeleteConfirmProducer;

    }

    // ===================================================== ( constructor end )

    public void execute(

        String userIp,
        String userAgent,
        Locale locale,
        AccountsDeleteConfirmDTO accountsDeleteConfirmDTO

    ) {

        // Password cleanup
        char[] password = accountsDeleteConfirmDTO.userPassword();

        try {

            // Find cached token
            var cachedToken = tokenVerificationCache.get(accountsDeleteConfirmDTO.token());

            // If token not exist, return expired link
            if (cachedToken == null || cachedToken.get() == null) {
                throw new DomainException(DomainExceptionEnum.ACCOUNTS_EXPIRED_LINK);
            }

            // Reason verification
            AccountsUpdateEmailCacheCommand cachedData = (AccountsUpdateEmailCacheCommand) cachedToken.get();

            if (
                cachedData.reason() == null ||
                cachedData.reason().trim().isEmpty() ||
                !AccountsUpdateEnum.ACCOUNTS_DELETE.getReasonCode().equals(cachedData.reason())
            ) {
                throw new DomainException(DomainExceptionEnum.ACCOUNTS_EXPIRED_LINK);
            }

            // User ID extract
            String idUser = cachedData.idUser();

            // Retrieve user data by ID and verify
            Map<String, Object> user = accountsKeycloakGetUser.getUserById(idUser);

            if (user == null || user.isEmpty()) {
                throw new DomainException(DomainExceptionEnum.ACCOUNTS_EXPIRED_LINK);
            }

            // Account banned
            if (Boolean.FALSE.equals(user.get("enabled"))) {
                throw new DomainException(DomainExceptionEnum.NO_PERMISSION_TO_ACCESS);
            }

            // Login in keycloak for password verify
            Map<String, Object> keycloakResponse = accountsKeycloakLogin.createUserLogin(
                (String) user.get("email"),
                new String(password)
            );

            // Null user verification
            if (keycloakResponse == null || keycloakResponse.isEmpty()) {
                throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
            }

            // Delete account execute
            accountsKeycloakDeleteUser.execute(idUser);

            // Create user account log
            AccountsCreateLogCommand logData = new AccountsCreateLogCommand(
                idUser,
                userIp,
                userAgent,
                AccountsUpdateEnum.ACCOUNTS_DELETE.getReasonCode(),
                ZonedDateTime.now(ZoneOffset.UTC).toInstant(),
                (String) user.get("email"),
                null
            );

            accountsEventProducer.accountLogProducer(logData);

            // Notification user for email
            accountsDeleteConfirmProducer.execute(
                locale,
                (String) user.get("email")
            );

            // Revoke cache
            tokenVerificationCache.evict(accountsDeleteConfirmDTO.token());

        }

        // Cleanup password
        finally {

            if (password != null) {
                java.util.Arrays.fill(password, '\0');
            }

        }

    }

}