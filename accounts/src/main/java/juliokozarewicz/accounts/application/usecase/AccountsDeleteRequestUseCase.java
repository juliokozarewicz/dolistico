package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsDeleteCacheCommand;
import juliokozarewicz.accounts.application.enums.AccountsUpdateEnum;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUser;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsDeleteRequestProducer;
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
public class AccountsDeleteRequestUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${DELETE_ACCOUNT_URL}")
    private String deleteAccountBaseURL;
    // -------------------------------------------------------------------------

    private final Encryption encryption;
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;
    private final CacheManager cacheManager;
    private final Cache tokenVerificationCache;
    private final AccountsDeleteRequestProducer accountsDeleteRequestProducer;

    public AccountsDeleteRequestUseCase(

        Encryption encryption,
        AccountsKeycloakGetUser accountsKeycloakGetUser,
        CacheManager cacheManager,
        AccountsDeleteRequestProducer accountsDeleteRequestProducer

    ) {

        this.encryption = encryption;
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;
        this.cacheManager = cacheManager;
        this.tokenVerificationCache = cacheManager.getCache("accounts.tokenVerificationCache");
        this.accountsDeleteRequestProducer = accountsDeleteRequestProducer;

    }

    // ===================================================== ( constructor end )

    public void execute(

        Locale locale,
        UUID idUser

    ) {

        // Generate token for verification
        String generatedToken = encryption.generate512Hex();

        // Get user by id
        Map<String, Object> existingUser = accountsKeycloakGetUser.getUserById(
            idUser.toString()
        );

        // Null verification
        if ( existingUser == null ) {
            throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
        }

        // Account banned
        if ( Boolean.FALSE.equals(existingUser.get("enabled")) ) {
            throw new DomainException(DomainExceptionEnum.NO_PERMISSION_TO_ACCESS);
        }

        // Create cache command
        AccountsDeleteCacheCommand cacheCommand = new AccountsDeleteCacheCommand(
            (String) existingUser.get("id"),
            AccountsUpdateEnum.ACCOUNTS_DELETE.getReasonCode()
        );

        // save token with email in cache
        tokenVerificationCache.put(generatedToken, cacheCommand);

        // Create URL with token
        String DeleteAccountdURL = UriComponentsBuilder
            .fromUriString(deleteAccountBaseURL)
            .queryParam("token", generatedToken)
            .build()
            .toUriString();

        // Create email message with URL
        accountsDeleteRequestProducer.execute(
            locale,
            (String) existingUser.get("email"),
            DeleteAccountdURL
        );

    }

}