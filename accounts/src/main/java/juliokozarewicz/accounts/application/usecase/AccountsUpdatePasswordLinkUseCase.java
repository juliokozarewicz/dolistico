package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsUpdatePasswordCacheCommand;
import juliokozarewicz.accounts.application.command.AccountsUpdatePasswordLinkCommand;
import juliokozarewicz.accounts.application.enums.AccountsUpdateEnum;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUser;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsUpdatePasswordLinkProducer;
import juliokozarewicz.accounts.infrastructure.security.Encryption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Locale;
import java.util.Map;

@Service
public class AccountsUpdatePasswordLinkUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${UPDATE_PASSWORD_URL}")
    private String updatePasswordBaseURL;
    // -------------------------------------------------------------------------

    private final Encryption encryption;
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;
    private final CacheManager cacheManager;
    private final Cache tokenVerificationCache;
    private final AccountsUpdatePasswordLinkProducer accountsUpdatePasswordLinkProducer;

    public AccountsUpdatePasswordLinkUseCase(

        Encryption encryption,
        AccountsKeycloakGetUser accountsKeycloakGetUser,
        CacheManager cacheManager,
        AccountsUpdatePasswordLinkProducer accountsUpdatePasswordLinkProducer

    ) {

        this.encryption = encryption;
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;
        this.cacheManager = cacheManager;
        this.tokenVerificationCache = cacheManager.getCache("accounts.tokenVerificationCache");
        this.accountsUpdatePasswordLinkProducer = accountsUpdatePasswordLinkProducer;

    }

    // ===================================================== ( constructor end )

    public void execute(

        Locale locale,
        AccountsUpdatePasswordLinkCommand accountsUpdatePasswordLinkCommand

    ) {

        // Generate token for verification
        String generatedToken = encryption.generate512Hex();

        // Get user id by email
        Map<String, Object> existingUser = accountsKeycloakGetUser.getUserByEmail(
            accountsUpdatePasswordLinkCommand.email()
        );

        // Null verification
        if ( existingUser == null ) { return; }

        // Account banned
        if ( Boolean.FALSE.equals(existingUser.get("enabled")) ) {
            throw new DomainException(DomainExceptionEnum.NO_PERMISSION_TO_ACCESS);
        }

        // Create cache command
        AccountsUpdatePasswordCacheCommand cacheCommand = new AccountsUpdatePasswordCacheCommand(
            (String) existingUser.get("id"),
            AccountsUpdateEnum.ACCOUNTS_UPDATE_PASSWORD.getReasonCode()
        );

        // save token with email in cache
        tokenVerificationCache.put(generatedToken, cacheCommand);

        // Create URL with token
        String updatePasswordURL = UriComponentsBuilder
            .fromUriString(updatePasswordBaseURL)
            .queryParam("token", generatedToken)
            .build()
            .toUriString();

        // Create email message with URL
        accountsUpdatePasswordLinkProducer.execute(
            locale,
            accountsUpdatePasswordLinkCommand.email(),
            updatePasswordURL
        );

    }

}