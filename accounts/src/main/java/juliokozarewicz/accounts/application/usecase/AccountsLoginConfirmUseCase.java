package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsLoginCacheCommand;
import juliokozarewicz.accounts.application.command.AccountsLoginConfirmCommand;
import juliokozarewicz.accounts.application.enums.AccountsUpdateEnum;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUser;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakLogin;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakUpdateUser;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsUserBannedProducer;
import juliokozarewicz.accounts.infrastructure.security.Encryption;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

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
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;
    private final AccountsUserBannedProducer accountsUserBannedProducer;
    private final AccountsKeycloakLogin accountsKeycloakLogin;
    private final Encryption encryption;

    public AccountsLoginConfirmUseCase(

        CacheManager cacheManager,
        AccountsKeycloakUpdateUser accountsKeycloakUpdateUser,
        AccountsKeycloakGetUser accountsKeycloakGetUser,
        AccountsUserBannedProducer accountsUserBannedProducer,
        AccountsKeycloakLogin accountsKeycloakLogin,
        Encryption encryption

    ) {

        this.cacheManager = cacheManager;
        this.accountsKeycloakUpdateUser = accountsKeycloakUpdateUser;
        this.tokenVerificationCache = cacheManager.getCache("accounts.tokenVerificationCache");
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;
        this.accountsUserBannedProducer = accountsUserBannedProducer;
        this.encryption = encryption;
        this.accountsKeycloakLogin = accountsKeycloakLogin;

    }

    // ===================================================== ( constructor end )

    public Map<String, Object> execute(

        Locale locale,
        AccountsLoginConfirmCommand accountsLoginConfirmCommand

    ) {

        // Find cached token
        var cachedToken = tokenVerificationCache.get(accountsLoginConfirmCommand.token());

        // If token not exist, return invalid credentials
        if ( cachedToken == null ) {
            throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
        }

        // Reason verification
        AccountsLoginCacheCommand cachedData = (AccountsLoginCacheCommand) cachedToken.get();

        if (
            !AccountsUpdateEnum.ACCOUNTS_LOGIN.getReasonCode()
            .equals(cachedData.reason())
        ) {
            throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
        }

        // Compares the provided PIN with the stored PIN (decrypted)
        boolean pinMatch = cachedData.pin().equals(accountsLoginConfirmCommand.pin());

        // If the PIN doesn't match, return a invalid PIN code
        if ( !pinMatch ) {
            throw new DomainException(DomainExceptionEnum.ACCOUNTS_INVALID_PIN);
        }

        // ##### Retrieve credentials from the refresh token stored in the cache (decrypted)

        // ##### If the credentials are null, return invalid credentials.

        // ##### Email notification for new device login

        // Return credentials
        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("access", "access-token-encrypted");
        response.put("refresh", "refresh-token-encrypted");
        response.put("accessExpiresIn", 123123);
        response.put("RefreshExpiresIn", 123123);
        return response;

    }

}