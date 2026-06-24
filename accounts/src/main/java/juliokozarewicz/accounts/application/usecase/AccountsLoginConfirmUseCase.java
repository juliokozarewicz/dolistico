package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsCreateLogCommand;
import juliokozarewicz.accounts.application.command.AccountsLoginCacheCommand;
import juliokozarewicz.accounts.application.command.AccountsLoginConfirmCommand;
import juliokozarewicz.accounts.application.enums.AccountsUpdateEnum;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUser;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakLogin;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakUpdateUser;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsEventProducer;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsLoginDeviceInfoProducer;
import juliokozarewicz.accounts.infrastructure.security.Encryption;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Map;

@Service
public class AccountsLoginConfirmUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final CacheManager cacheManager;
    private final Cache tokenVerificationCache;
    private final AccountsKeycloakUpdateUser accountsKeycloakUpdateUser;
    private final AccountsKeycloakLogin accountsKeycloakLogin;
    private final Encryption encryption;
    private final AccountsEventProducer accountsEventProducer;
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;
    private final AccountsLoginDeviceInfoProducer accountsLoginDeviceInfoProducer;
    private final MessageSource messageSource;

    public AccountsLoginConfirmUseCase(

        CacheManager cacheManager,
        AccountsKeycloakUpdateUser accountsKeycloakUpdateUser,
        AccountsKeycloakLogin accountsKeycloakLogin,
        Encryption encryption,
        AccountsEventProducer accountsEventProducer,
        AccountsKeycloakGetUser accountsKeycloakGetUser,
        AccountsLoginDeviceInfoProducer accountsLoginDeviceInfoProducer,
        MessageSource messageSource

    ) {

        this.cacheManager = cacheManager;
        this.accountsKeycloakUpdateUser = accountsKeycloakUpdateUser;
        this.tokenVerificationCache = cacheManager.getCache("accounts.tokenVerificationCache");
        this.encryption = encryption;
        this.accountsKeycloakLogin = accountsKeycloakLogin;
        this.accountsEventProducer = accountsEventProducer;
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;
        this.accountsLoginDeviceInfoProducer = accountsLoginDeviceInfoProducer;
        this.messageSource = messageSource;

    }

    // ===================================================== ( constructor end )

    public Map<String, Object> execute(

        String userIp,
        String userAgent,
        Locale locale,
        AccountsLoginConfirmCommand accountsLoginConfirmCommand

    ) {

        // Find cached token
        var cachedToken = tokenVerificationCache.get(accountsLoginConfirmCommand.userLoginToken());

        // Null + type safety check
        if (cachedToken == null || cachedToken.get() == null) {
            throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
        }

        Object raw = cachedToken.get();

        if (!(raw instanceof AccountsLoginCacheCommand cachedData)) {
            throw new DomainException(DomainExceptionEnum.ACCOUNTS_EXPIRED_LINK);
        }

        // User ID extract
        String idUser = cachedData.idUser();

        // Compares the provided PIN with the stored PIN (decrypted)
        boolean pinMatch = java.util.Objects.equals(
            encryption.decrypt(cachedData.pin()),
            accountsLoginConfirmCommand.pin()
        );

        // If the PIN doesn't match, return a invalid PIN code
        if ( !pinMatch ) {
            throw new DomainException(DomainExceptionEnum.ACCOUNTS_INVALID_PIN);
        }

        // Retrieve refresh token from cache and decrypt
        String refreshTokenEncrypted = cachedData.refreshToken();

        if (
            refreshTokenEncrypted == null ||
            refreshTokenEncrypted.trim().isEmpty()
        ) {
            throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
        }

        String refreshTokenDecrypted = encryption.decrypt(refreshTokenEncrypted);

        if (
            refreshTokenDecrypted == null ||
            refreshTokenDecrypted.trim().isEmpty()
        ) {
            throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
        }

        // Refresh credentials in Keycloak
        Map<String, Object> userCredentials = accountsKeycloakLogin.refreshUserLogin(refreshTokenDecrypted);

        // Null user verification
        if (userCredentials == null || userCredentials.isEmpty()) {
            throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
        }

        // Get credentials to userCredentials
        String accessToken = (String) userCredentials.get("access_token");
        String refreshToken = (String) userCredentials.get("refresh_token");
        Number expiresIn = (Number) userCredentials.get("expires_in");
        Number refreshExpiresIn = (Number) userCredentials.get("refresh_expires_in");

        // Create user account log
        AccountsCreateLogCommand logData = new AccountsCreateLogCommand(
            idUser,
            userIp,
            userAgent,
            AccountsUpdateEnum.ACCOUNTS_LOGIN.getReasonCode(),
            ZonedDateTime.now(ZoneOffset.UTC).toInstant(),
            null,
            null
        );

        accountsEventProducer.accountLogProducer(logData);

        // Update verify email
        accountsKeycloakUpdateUser.updateVerifyEmail(idUser);

        // Revoke cache
        tokenVerificationCache.evict(accountsLoginConfirmCommand.userLoginToken());

        // Email notification for new device login
        Map<String, Object> user = accountsKeycloakGetUser.getUserById(idUser);

        if (user == null || user.isEmpty()) {
            throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
        }

        // Account banned
        if ( Boolean.FALSE.equals(user.get("enabled") ) ) {
            throw new DomainException(DomainExceptionEnum.NO_PERMISSION_TO_ACCESS);
        }

        accountsLoginDeviceInfoProducer.execute(
            userIp,
            userAgent,
            locale,
            idUser,
            (String) user.get("email"),
            messageSource.getMessage("email_new_login_method_password", null, locale)
        );

        // Return credentials
        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("access", encryption.encrypt(accessToken));
        response.put("refresh", encryption.encrypt(refreshToken));
        response.put("accessExpiresAt", Instant.now().plusSeconds(expiresIn.longValue()));
        response.put("refreshExpiresAt", Instant.now().plusSeconds(refreshExpiresIn.longValue()));
        return response;

    }

}