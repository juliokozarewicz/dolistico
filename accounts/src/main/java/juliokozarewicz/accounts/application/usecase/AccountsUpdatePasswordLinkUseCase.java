package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsUpdatePasswordLinkCommand;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUser;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsEventProducer;
import juliokozarewicz.accounts.infrastructure.security.TokenGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Locale;

@Service
public class AccountsUpdatePasswordLinkUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${UPDATE_PASSWORD_URL}")
    private String updatePasswordBaseURL;
    // -------------------------------------------------------------------------

    private final TokenGenerator tokenGenerator;
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;
    private final CacheManager cacheManager;
    private final Cache tokenVerificationCache;
    private final AccountsEventProducer accountsEventProducer;

    public AccountsUpdatePasswordLinkUseCase(

        TokenGenerator tokenGenerator,
        AccountsKeycloakGetUser accountsKeycloakGetUser,
        CacheManager cacheManager,
        AccountsEventProducer accountsEventProducer

    ) {

        this.tokenGenerator = tokenGenerator;
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;
        this.cacheManager = cacheManager;
        this.tokenVerificationCache = cacheManager.getCache("accounts.tokenVerificationCache");
        this.accountsEventProducer = accountsEventProducer;

    }

    // ===================================================== ( constructor end )

    public void execute(

        Locale locale,
        AccountsUpdatePasswordLinkCommand accountsUpdatePasswordLinkCommand

    ) {

        // Generate token for verification
        String generatedToken = tokenGenerator.generate512Hex();

        // Get user id by email
        String existingUserId = accountsKeycloakGetUser.getUserByEmail(
            accountsUpdatePasswordLinkCommand.email()
        );

        // Null verification
        if ( existingUserId == null ) { return; }

        // save token with email in cache
        tokenVerificationCache.put(generatedToken, existingUserId);

        // Create URL with token
        String updatePasswordURL = UriComponentsBuilder
            .fromUriString(updatePasswordBaseURL)
            .queryParam("token", generatedToken)
            .build()
            .toUriString();

        // Create email message with URL
        accountsEventProducer.producerSendEmailLink(
            locale,
            accountsUpdatePasswordLinkCommand.email(),
            updatePasswordURL
        );

    }

}