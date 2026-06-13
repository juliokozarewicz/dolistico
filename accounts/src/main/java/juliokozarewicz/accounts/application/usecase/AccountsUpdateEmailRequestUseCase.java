package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsUpdateEmailCacheCommand;
import juliokozarewicz.accounts.application.command.AccountsUpdateEmailRequestCommand;
import juliokozarewicz.accounts.application.command.AccountsUpdatePasswordCacheCommand;
import juliokozarewicz.accounts.application.enums.AccountsUpdateEnum;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUser;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsUpdatePasswordRequestProducer;
import juliokozarewicz.accounts.infrastructure.security.Encryption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class AccountsUpdateEmailRequestUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${UPDATE_EMAIL_URL}")
    private String updateEmailBaseURL;
    // -------------------------------------------------------------------------

    private final Encryption encryption;
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;
    private final CacheManager cacheManager;
    private final Cache tokenVerificationCache;

    public AccountsUpdateEmailRequestUseCase(

        Encryption encryption,
        AccountsKeycloakGetUser accountsKeycloakGetUser,
        CacheManager cacheManager

    ) {

        this.encryption = encryption;
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;
        this.cacheManager = cacheManager;
        this.tokenVerificationCache = cacheManager.getCache("accounts.tokenVerificationCache");

    }

    // ===================================================== ( constructor end )

    public void execute(

        Locale locale,
        UUID idUser,
        AccountsUpdateEmailRequestCommand accountsUpdateEmailRequestCommand

    ) {

        // Get old user by id
        Map<String, Object> oldUser = accountsKeycloakGetUser.getUserById(
            idUser.toString()
        );

        // Old user not exist verification
        if ( oldUser == null ) {
            throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
        }

        // Get user id by new email
        Map<String, Object> newUser = accountsKeycloakGetUser.getUserByEmail(
            accountsUpdateEmailRequestCommand.newEmail()
        );

        // User already exist verification
        if ( newUser != null ) {
            throw new DomainException(DomainExceptionEnum.ACCOUNTS_USER_ALREADY_EXISTS);
        }

        // Account banned
        if ( Boolean.FALSE.equals(oldUser.get("enabled")) ) {
            throw new DomainException(DomainExceptionEnum.NO_PERMISSION_TO_ACCESS);
        }

        // Generate token for verification
        String generatedToken = encryption.generate512Hex();

        // Generate pin for verification
        String generatedPinRaw = encryption.generatePin();
        String generatedPinEncrypted = encryption.encrypt(generatedPinRaw);

        // Create cache command
        AccountsUpdateEmailCacheCommand cacheCommand = new AccountsUpdateEmailCacheCommand(
            (String) oldUser.get("id"),
            generatedPinEncrypted,
            accountsUpdateEmailRequestCommand.newEmail(),
            AccountsUpdateEnum.ACCOUNTS_UPDATE_PASSWORD.getReasonCode()
        );

        // save token with email in cache
        tokenVerificationCache.put(generatedToken, cacheCommand);

        // Create URL with token
        String updateEmailURL = UriComponentsBuilder
            .fromUriString(updateEmailBaseURL)
            .queryParam("token", generatedToken)
            .build()
            .toUriString();

        // ##### Create email message with URL
        accountsUpdateEmailRequestProducer.execute(
            locale,
            oldUser.get("email"),
            updateEmailURL,
            accountsUpdateEmailRequestCommand.newEmail(),
            generatedPinRaw
        );

    }

}