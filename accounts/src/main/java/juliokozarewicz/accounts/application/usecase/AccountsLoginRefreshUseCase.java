package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.adapter.rest.dto.AccountsLoginRefreshDTO;
import juliokozarewicz.accounts.application.command.AccountsCreateLogCommand;
import juliokozarewicz.accounts.application.command.AccountsLoginCacheCommand;
import juliokozarewicz.accounts.application.command.AccountsLoginConfirmCommand;
import juliokozarewicz.accounts.application.command.AccountsLoginRefreshCommand;
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
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Map;

@Service
public class AccountsLoginRefreshUseCase {

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

    public AccountsLoginRefreshUseCase(

        CacheManager cacheManager,
        AccountsKeycloakUpdateUser accountsKeycloakUpdateUser,
        AccountsKeycloakLogin accountsKeycloakLogin,
        Encryption encryption,
        AccountsEventProducer accountsEventProducer,
        AccountsKeycloakGetUser accountsKeycloakGetUser,
        AccountsLoginDeviceInfoProducer accountsLoginDeviceInfoProducer

    ) {

        this.cacheManager = cacheManager;
        this.accountsKeycloakUpdateUser = accountsKeycloakUpdateUser;
        this.tokenVerificationCache = cacheManager.getCache("accounts.tokenVerificationCache");
        this.encryption = encryption;
        this.accountsKeycloakLogin = accountsKeycloakLogin;
        this.accountsEventProducer = accountsEventProducer;
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;
        this.accountsLoginDeviceInfoProducer = accountsLoginDeviceInfoProducer;

    }

    // ===================================================== ( constructor end )

    public Map<String, Object> execute(

        Locale locale,
        AccountsLoginRefreshCommand accountsLoginRefreshCommand

    ) {

        // Decrypt refresh token
        String refreshTokenDecrypted = encryption.decrypt(accountsLoginRefreshCommand.refreshToken());

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

        // User ID extract
        String idUser = accountsKeycloakLogin.idUserExtract(accessToken);

        // Account banned
        if ( accountsKeycloakGetUser.isAccountBannedById(idUser) ) {
            throw new DomainException(DomainExceptionEnum.NO_PERMISSION_TO_ACCESS);
        }

        // Return credentials
        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("access", encryption.encrypt(accessToken));
        response.put("refresh", encryption.encrypt(refreshToken));
        response.put("accessExpiresAt", Instant.now().plusSeconds(expiresIn.longValue()));
        response.put("refreshExpiresAt", Instant.now().plusSeconds(refreshExpiresIn.longValue()));
        return response;

    }

}