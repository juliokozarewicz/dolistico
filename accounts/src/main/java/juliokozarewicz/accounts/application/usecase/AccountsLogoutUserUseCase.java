package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsLogoutUserCommand;
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
import java.util.Map;

@Service
public class AccountsLogoutUserUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsKeycloakLogin accountsKeycloakLogin;
    private final Encryption encryption;

    public AccountsLogoutUserUseCase(

        AccountsKeycloakLogin accountsKeycloakLogin,
        Encryption encryption

    ) {

        this.encryption = encryption;
        this.accountsKeycloakLogin = accountsKeycloakLogin;

    }

    // ===================================================== ( constructor end )

    public void execute(

        AccountsLogoutUserCommand accountsLogoutUserCommand

    ) {

        // Decrypt refresh token
        String refreshTokenDecrypted = encryption.decrypt(accountsLogoutUserCommand.refreshToken());

        if (
            refreshTokenDecrypted == null ||
            refreshTokenDecrypted.trim().isEmpty()
        ) {
            throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
        }

        // Refresh credentials in Keycloak
        accountsKeycloakLogin.logoutUser(refreshTokenDecrypted);

    }

}