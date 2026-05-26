package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.adapter.rest.dto.AccountsUpdatePasswordDTO;
import juliokozarewicz.accounts.application.command.AccountsUpdatePasswordCacheCommand;
import juliokozarewicz.accounts.application.enums.AccountsUpdateEnum;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakUpdateUser;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsUpdateEmailProducer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
public class AccountsUpdatePasswordUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final CacheManager cacheManager;
    private final Cache tokenVerificationCache;
    private final AccountsUpdateEmailProducer accountsUpdateEmailProducer;
    private final AccountsKeycloakUpdateUser accountsKeycloakUpdateUser;

    public AccountsUpdatePasswordUseCase(

        CacheManager cacheManager,
        AccountsUpdateEmailProducer accountsUpdateEmailProducer,
        AccountsKeycloakUpdateUser accountsKeycloakUpdateUser

    ) {

        this.cacheManager = cacheManager;
        this.accountsUpdateEmailProducer = accountsUpdateEmailProducer;
        this.accountsKeycloakUpdateUser = accountsKeycloakUpdateUser;
        this.tokenVerificationCache = cacheManager.getCache("accounts.tokenVerificationCache");

    }

    // ===================================================== ( constructor end )

    public void execute(

        Locale locale,
        AccountsUpdatePasswordDTO accountsUpdatePasswordDTO

    ) {

        // Retrieve stored token cache
        var cachedToken = tokenVerificationCache.get(accountsUpdatePasswordDTO.token());

        // Null verification
        if (cachedToken == null) {
            throw new DomainException(DomainExceptionEnum.ACCOUNTS_EXPIRED_LINK);
        }

        // Reason verification
        AccountsUpdatePasswordCacheCommand cachedData = (AccountsUpdatePasswordCacheCommand) cachedToken.get();

        if (
            !AccountsUpdateEnum.ACCOUNTS_UPDATE_PASSWORD.getReasonCode()
            .equals( cachedData.reason() )
        ) {
            throw new DomainException(DomainExceptionEnum.ACCOUNTS_EXPIRED_LINK);
        }

        // Update password via Keycloak
        var result = accountsKeycloakUpdateUser.updatePassword(
            cachedData.idUser(),
            accountsUpdatePasswordDTO.newPassword()
        );

        // Clean token cache
        tokenVerificationCache.evict(accountsUpdatePasswordDTO.token());

        // Send email notification
        accountsUpdateEmailProducer.execute(
            locale,
            result.get("email").toString()
        );

    }

}