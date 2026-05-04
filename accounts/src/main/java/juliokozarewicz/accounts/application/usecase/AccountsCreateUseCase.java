package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsCreateCommand;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakCreateUser;
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

    public AccountsCreateUseCase(

        AccountsKeycloakTokenProvider accountsTokenProvider,
        AccountsKeycloakCreateUser accountsKeycloakCreateUser

    ) {

        this.accountsTokenProvider = accountsTokenProvider;
        this.accountsKeycloakCreateUser = accountsKeycloakCreateUser;

    }

    // ===================================================== ( constructor end )

    public String execute(

        AccountsCreateCommand command

    ) {

        // Get client token
        String clientToken = accountsTokenProvider.getAccessToken();

        // Create user
        String iduser = accountsKeycloakCreateUser.execute (
            clientToken,
            command.email(),
            command.password()
        );

        return iduser;

    }

}