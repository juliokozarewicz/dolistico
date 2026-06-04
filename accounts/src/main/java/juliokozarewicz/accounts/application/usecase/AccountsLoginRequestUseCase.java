package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsLoginCacheCommand;
import juliokozarewicz.accounts.application.command.AccountsLoginRequestCommand;
import juliokozarewicz.accounts.application.enums.AccountsUpdateEnum;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUser;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakLogin;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsLoginRequestProducer;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsUserBannedProducer;
import juliokozarewicz.accounts.infrastructure.security.Encryption;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service
public class AccountsLoginRequestUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final CacheManager cacheManager;
    private final Cache tokenVerificationCache;
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;
    private final AccountsUserBannedProducer accountsUserBannedProducer;
    private final AccountsKeycloakLogin accountsKeycloakLogin;
    private final Encryption encryption;
    private final AccountsLoginRequestProducer accountsLoginRequestProducer;

    public AccountsLoginRequestUseCase(

        CacheManager cacheManager,
        AccountsKeycloakGetUser accountsKeycloakGetUser,
        AccountsUserBannedProducer accountsUserBannedProducer,
        AccountsKeycloakLogin accountsKeycloakLogin,
        Encryption encryption,
        AccountsLoginRequestProducer accountsLoginRequestProducer

    ) {

        this.cacheManager = cacheManager;
        this.tokenVerificationCache = cacheManager.getCache("accounts.tokenVerificationCache");
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;
        this.accountsUserBannedProducer = accountsUserBannedProducer;
        this.encryption = encryption;
        this.accountsKeycloakLogin = accountsKeycloakLogin;
        this.accountsLoginRequestProducer = accountsLoginRequestProducer;

    }

    // ===================================================== ( constructor end )

    public String execute(

        Locale locale,
        AccountsLoginRequestCommand accountsLoginRequestCommand

    ) {

        // Password cleanup
        char[] password = accountsLoginRequestCommand.userPassword();

        try {

            // Account banned (send email to user)
            String existingUserId = accountsKeycloakGetUser.getUserByEmail(accountsLoginRequestCommand.email());

            if ( existingUserId != null && accountsKeycloakGetUser.isAccountBannedById(existingUserId) ) {
                accountsUserBannedProducer.execute(locale, accountsLoginRequestCommand.email());
                throw new DomainException(DomainExceptionEnum.NO_PERMISSION_TO_ACCESS);
            }

            // Get user refresh token by auth (email + pass) from Keycloak
            Map<String, Object> keycloakResponse = accountsKeycloakLogin.createUserLogin(
                accountsLoginRequestCommand.email(),
                new String(password)
            );

            // Null user verification
            if (keycloakResponse == null || keycloakResponse.isEmpty()) {
                throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
            }

            // Extract access token
            String accessToken = (String) keycloakResponse.get("access_token");

            if (accessToken == null || accessToken.isBlank()) {
                throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
            }

            // Extract refresh token
            String refreshTokenRaw = (String) keycloakResponse.get("refresh_token");

            if (refreshTokenRaw == null || refreshTokenRaw.isBlank()) {
                throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
            }

            String refreshTokenEncrypted = encryption.encrypt(refreshTokenRaw);

            // Create user login request token
            String userLoginToken = encryption.generate512Hex();

            // Create user pin
            String generatedPinRaw = encryption.generatePin();
            String generatedPinEncrypted = encryption.encrypt(generatedPinRaw);

            // Storage token + pin + user refresh token encrypted + reason in cache
            AccountsLoginCacheCommand loginRequestData = new AccountsLoginCacheCommand(
                generatedPinEncrypted,
                refreshTokenEncrypted,
                AccountsUpdateEnum.ACCOUNTS_LOGIN.getReasonCode()
            );

            tokenVerificationCache.put(userLoginToken, loginRequestData);

            // Send email to user with url (token + pin)
            accountsLoginRequestProducer.execute(
                locale,
                accountsLoginRequestCommand.email(),
                generatedPinRaw
            );

            return userLoginToken;

        }

        // Cleanup password
        finally {

            if (password != null) {
                java.util.Arrays.fill(password, '\0');
            }

        }

    }

}