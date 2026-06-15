package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.adapter.rest.dto.AccountsUpdateEmailConfirmDTO;
import juliokozarewicz.accounts.application.command.AccountsCreateLogCommand;
import juliokozarewicz.accounts.application.command.AccountsUpdateEmailCacheCommand;
import juliokozarewicz.accounts.application.enums.AccountsUpdateEnum;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUser;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakLogoutUserGlobally;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakUpdateUser;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsEventProducer;
import juliokozarewicz.accounts.infrastructure.security.Encryption;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Map;

@Service
public class AccountsUpdateEmailConfirmUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final CacheManager cacheManager;
    private final Cache tokenVerificationCache;
    private final AccountsKeycloakUpdateUser accountsKeycloakUpdateUser;
    private final Encryption encryption;
    private final AccountsEventProducer accountsEventProducer;
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;
    private final AccountsKeycloakLogoutUserGlobally accountsKeycloakLogoutUserGlobally;

    public AccountsUpdateEmailConfirmUseCase(

        CacheManager cacheManager,
        AccountsKeycloakUpdateUser accountsKeycloakUpdateUser,
        Encryption encryption,
        AccountsEventProducer accountsEventProducer,
        AccountsKeycloakGetUser accountsKeycloakGetUser,
        AccountsKeycloakLogoutUserGlobally accountsKeycloakLogoutUserGlobally

    ) {

        this.cacheManager = cacheManager;
        this.accountsKeycloakUpdateUser = accountsKeycloakUpdateUser;
        this.tokenVerificationCache = cacheManager.getCache("accounts.tokenVerificationCache");
        this.encryption = encryption;
        this.accountsEventProducer = accountsEventProducer;
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;
        this.accountsKeycloakLogoutUserGlobally = accountsKeycloakLogoutUserGlobally;

    }

    // ===================================================== ( constructor end )

    public void execute(

        String userIp,
        String userAgent,
        Locale locale,
        AccountsUpdateEmailConfirmDTO accountsUpdateEmailConfirmDTO

    ) {

        // Find cached token
        var cachedToken = tokenVerificationCache.get(accountsUpdateEmailConfirmDTO.token());

        // If token not exist, return invalid credentials
        if ( cachedToken == null || cachedToken.get() == null ) {
            throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
        }

        // Reason verification
        AccountsUpdateEmailCacheCommand cachedData = (AccountsUpdateEmailCacheCommand) cachedToken.get();

        if (
            cachedData.reason() == null ||
            cachedData.reason().trim().isEmpty() ||
            !AccountsUpdateEnum.ACCOUNTS_UPDATE_EMAIL.getReasonCode().equals(cachedData.reason())
        ) {
            throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
        }

        // User ID extract
        String idUser = cachedData.idUser();

        // Account banned
        if ( accountsKeycloakGetUser.isAccountBannedById(idUser) ) {
            throw new DomainException(DomainExceptionEnum.NO_PERMISSION_TO_ACCESS);
        }

        // Compares the provided PIN with the stored PIN (decrypted)
        boolean pinMatch = java.util.Objects.equals(
            encryption.decrypt(cachedData.pin()),
            accountsUpdateEmailConfirmDTO.pin()
        );

        // If the PIN doesn't match, return a invalid PIN code
        if ( !pinMatch ) {
            throw new DomainException(DomainExceptionEnum.ACCOUNTS_INVALID_PIN);
        }

        // Retrieve current user data by ID and verify
        Map<String, Object> user = accountsKeycloakGetUser.getUserById(idUser);

        if (user == null || user.isEmpty()) {
            throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
        }

        // Validate that the new email is different from the current email
        String oldEmail = (String) user.get("email");

        if ( oldEmail != null && oldEmail.equalsIgnoreCase(cachedData.newEmail()) ) {
            throw new DomainException(DomainExceptionEnum.ACCOUNTS_USER_ALREADY_EXISTS);
        }

        // Validate that the new email is not already associated with another account
        Map<String, Object> existingUser = accountsKeycloakGetUser.getUserByEmail(cachedData.newEmail());

        if (existingUser != null && !idUser.equals(existingUser.get("id")) ) {
            throw new DomainException(DomainExceptionEnum.ACCOUNTS_USER_ALREADY_EXISTS);
        }

        // Update user email address
        accountsKeycloakUpdateUser.updateEmail(
            idUser,
            cachedData.newEmail()
        );

        // Create user account log
        AccountsCreateLogCommand logData = new AccountsCreateLogCommand(
            idUser,
            userIp,
            userAgent,
            AccountsUpdateEnum.ACCOUNTS_UPDATE_EMAIL.getReasonCode(),
            ZonedDateTime.now(ZoneOffset.UTC).toInstant(),
            oldEmail,
            cachedData.newEmail()
        );

        accountsEventProducer.accountLogProducer(logData);

        // ##### Notification for both email

        // Revoke all active sessions to enforce re-authentication
        accountsKeycloakLogoutUserGlobally.execute(idUser);

        // Revoke cache
        tokenVerificationCache.evict(accountsUpdateEmailConfirmDTO.token());

    }

}