package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsUpdateEmailCacheCommand;
import juliokozarewicz.accounts.application.command.AccountsUpdateEmailRequestCommand;
import juliokozarewicz.accounts.application.enums.AccountsUpdateEnum;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.domain.repository.AccountsConfigRepository;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUser;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsUpdateEmailRequestPINProducer;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsUpdateEmailRequestURLProducer;
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
    // -------------------------------------------------------------------------

    private final Encryption encryption;
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;
    private final CacheManager cacheManager;
    private final Cache tokenVerificationCache;
    private final AccountsUpdateEmailRequestURLProducer accountsUpdateEmailRequestURLProducer;
    private final AccountsUpdateEmailRequestPINProducer accountsUpdateEmailRequestPINProducer;
    private final AccountsConfigRepository accountsConfigRepository;

    public AccountsUpdateEmailRequestUseCase(

        Encryption encryption,
        AccountsKeycloakGetUser accountsKeycloakGetUser,
        CacheManager cacheManager,
        AccountsUpdateEmailRequestURLProducer accountsUpdateEmailRequestURLProducer,
        AccountsUpdateEmailRequestPINProducer accountsUpdateEmailRequestPINProducer,
        AccountsConfigRepository accountsConfigRepository

    ) {

        this.encryption = encryption;
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;
        this.cacheManager = cacheManager;
        this.tokenVerificationCache = cacheManager.getCache("accounts.tokenVerificationCache");
        this.accountsUpdateEmailRequestURLProducer = accountsUpdateEmailRequestURLProducer;
        this.accountsUpdateEmailRequestPINProducer = accountsUpdateEmailRequestPINProducer;
        this.accountsConfigRepository = accountsConfigRepository;

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
            AccountsUpdateEnum.ACCOUNTS_UPDATE_EMAIL.getReasonCode()
        );

        // save token with email in cache
        tokenVerificationCache.put(generatedToken, cacheCommand);

        // Create URL with token
        String updateEmailBaseURL = accountsConfigRepository.findByConfigName("update_email_url")
            .orElseThrow(() ->
                new DomainException(DomainExceptionEnum.INTERNAL_SERVER_ERROR)
            )
        .getConfigValue();

        String updateEmailURL = UriComponentsBuilder
            .fromUriString(updateEmailBaseURL)
            .queryParam("token", generatedToken)
            .build()
            .toUriString();

        // Create email message with URL to old email
        accountsUpdateEmailRequestURLProducer.execute(
            locale,
            oldUser.get("email").toString(),
            updateEmailURL
        );

        // Create email message with PIN to the new email
        accountsUpdateEmailRequestPINProducer.execute(
            locale,
            accountsUpdateEmailRequestCommand.newEmail(),
            generatedPinRaw
        );

    }

}