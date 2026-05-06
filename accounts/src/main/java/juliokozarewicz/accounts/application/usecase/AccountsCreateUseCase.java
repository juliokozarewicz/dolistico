package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsCreateCommand;
import juliokozarewicz.accounts.domain.entity.AccountsProfileEntity;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.domain.repository.AccountsProfileRepository;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakCreateUser;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakDeleteUser;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUser;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakClientTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.util.UUID;

@Service
public class AccountsCreateUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsCreateUseCase.class);
    private final AccountsKeycloakClientTokenProvider accountsKeycloakClientTokenProvider;
    private final AccountsKeycloakCreateUser accountsKeycloakCreateUser;
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;
    private final AccountsKeycloakDeleteUser accountsKeycloakDeleteUser;
    private final AccountsProfileRepository accountsProfileRepository;

    public AccountsCreateUseCase (

        AccountsKeycloakClientTokenProvider accountsKeycloakClientTokenProvider,
        AccountsKeycloakCreateUser accountsKeycloakCreateUser,
        AccountsKeycloakGetUser accountsKeycloakGetUser,
        AccountsKeycloakDeleteUser accountsKeycloakDeleteUser,
        AccountsProfileRepository accountsProfileRepository

    ) {

        this.accountsKeycloakClientTokenProvider = accountsKeycloakClientTokenProvider;
        this.accountsKeycloakCreateUser = accountsKeycloakCreateUser;
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;
        this.accountsKeycloakDeleteUser = accountsKeycloakDeleteUser;
        this.accountsProfileRepository = accountsProfileRepository;

    }

    // ===================================================== ( constructor end )

    public void execute(

        AccountsCreateCommand command

    ) {

        // Get client token
        String clientToken = accountsKeycloakClientTokenProvider.getAccessToken();

        // Password cleanup
        char[] password = command.userPassword();

        // Recent account
        String idUserCreated = null;

        try {

            // Check if user already exists
            String existingUserId = accountsKeycloakGetUser.getUserByEmail(
                clientToken,
                command.email()
            );

            // If user already exists, do nothing
            if ( existingUserId != null ) { return; }

            // Create user
            idUserCreated = accountsKeycloakCreateUser.execute(
                clientToken,
                command.email(),
                password
            );

            // Create user profile table

            Instant timeStamp = Instant.now();

            AccountsProfileEntity profile = new AccountsProfileEntity(
                UUID.fromString(idUserCreated),
                timeStamp,
                timeStamp,
                null,
                command.fullName(),
                null,
                null,
                null,
                null,
                null,
                null,
                null
            );

            accountsProfileRepository.save(profile);

        }

        // Keycloak conflict error - ignore (409)
        catch (WebClientResponseException.Conflict ignored) { return; }

        // Fallback (500)
        catch (Exception e) {

            logger.error("Keycloak error [ AccountsCreateUseCase.execute() ]: " + e);

            // Rollback in a recent account with error
            if (idUserCreated != null) {
                accountsKeycloakDeleteUser.execute(clientToken, idUserCreated);
            }

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

        // Cleanup password
        finally {

            if (password != null) {
                java.util.Arrays.fill(password, '\0');
            }

        }

    }

}