package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsCreateCommand;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakCreateUser;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUserByEmail;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AccountsCreateUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsKeycloakTokenProvider accountsTokenProvider;
    private final AccountsKeycloakCreateUser accountsKeycloakCreateUser;
    private final AccountsKeycloakGetUserByEmail accountsKeycloakGetUserByEmail;

    public AccountsCreateUseCase (

        AccountsKeycloakTokenProvider accountsTokenProvider,
        AccountsKeycloakCreateUser accountsKeycloakCreateUser,
        AccountsKeycloakGetUserByEmail accountsKeycloakGetUserByEmail

    ) {

        this.accountsTokenProvider = accountsTokenProvider;
        this.accountsKeycloakCreateUser = accountsKeycloakCreateUser;
        this.accountsKeycloakGetUserByEmail = accountsKeycloakGetUserByEmail;

    }

    // ===================================================== ( constructor end )

    public void execute(

        AccountsCreateCommand command

    ) {

        // Get client token
        String clientToken = accountsTokenProvider.getAccessToken();

        // Check if user already exists
        String existingUserId = accountsKeycloakGetUserByEmail.execute(
            clientToken,
            command.email()
        );

        // If user already exists, do nothing
        if ( existingUserId != null ) {
            return;
        }

        // Create user
        String iduser = accountsKeycloakCreateUser.execute (
            clientToken,
            command.email(),
            command.password()
        );

    }

}