package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsCreateCommand;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakCreateUser;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakDeleteUser;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUser;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakClientTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class AccountsCreateUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsKeycloakCreateUser.class);
    private final AccountsKeycloakClientTokenProvider accountsTokenProvider;
    private final AccountsKeycloakCreateUser accountsKeycloakCreateUser;
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;
    private final AccountsKeycloakDeleteUser accountsKeycloakDeleteUser;

    public AccountsCreateUseCase (

        AccountsKeycloakClientTokenProvider accountsTokenProvider,
        AccountsKeycloakCreateUser accountsKeycloakCreateUser,
        AccountsKeycloakGetUser accountsKeycloakGetUser,
        AccountsKeycloakDeleteUser accountsKeycloakDeleteUser

    ) {

        this.accountsTokenProvider = accountsTokenProvider;
        this.accountsKeycloakCreateUser = accountsKeycloakCreateUser;
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;
        this.accountsKeycloakDeleteUser = accountsKeycloakDeleteUser;

    }

    // ===================================================== ( constructor end )

    public void execute(

        AccountsCreateCommand command

    ) {

        // Get client token
        String clientToken = accountsTokenProvider.getAccessToken();

        // Password cleanup
        char[] password = command.password();

        try {

            // Check if user already exists
            String existingUserId = accountsKeycloakGetUser.getUserByEmail(
                clientToken,
                command.email()
            );

            // If user already exists, do nothing
            if ( existingUserId != null ) { return; }

            // Create user
            String idUserCreated = accountsKeycloakCreateUser.execute(
                clientToken,
                command.email(),
                password
            );

            // ##### Create user profile table

        }

        // Conflict error (409)
        catch (WebClientResponseException.Conflict ignored) { return; }

        // Fallback (500)
        catch (Exception e) {

            logger.error("Error accessing Keycloak [ AccountsKeycloakCreateUser.execute() ]: " + e);
            accountsKeycloakDeleteUser.execute(clientToken, command.email());
            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}