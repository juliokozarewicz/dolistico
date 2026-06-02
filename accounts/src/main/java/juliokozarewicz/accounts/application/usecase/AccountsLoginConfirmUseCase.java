package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsLoginConfirmCommand;
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

        // ##### Find cached token

        // ##### If token not exist, return invalid token code

        // ##### Compares the provided PIN with the stored PIN (decrypted)

        // ##### If the PIN doesn't match, return a invalid PIN code

        // ##### Retrieve credentials from the refresh token stored in the cache (decrypted)

        // ##### If the credentials are null, return invalid credentials.

        // Return credentials
        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("access", "access-token-encrypted");
        response.put("refresh", "refresh-token-encrypted");
        response.put("accessExpiresIn", 123123);
        response.put("RefreshExpiresIn", 123123);
        return response;

    }

}