package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsUpdatePasswordLinkCommand;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUser;
import juliokozarewicz.accounts.infrastructure.security.TokenGenerator;
import org.springframework.stereotype.Service;

@Service
public class AccountsUpdatePasswordLinkUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final TokenGenerator tokenGenerator;
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;

    public AccountsUpdatePasswordLinkUseCase(

        TokenGenerator tokenGenerator,
        AccountsKeycloakGetUser accountsKeycloakGetUser

    ) {

        this.tokenGenerator = tokenGenerator;
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;

    }

    // ===================================================== ( constructor end )

    public void execute(

        AccountsUpdatePasswordLinkCommand accountsUpdatePasswordLinkCommand

    ) {

        // Generate token for verification
        String generatedToken = tokenGenerator.generate512Hex();

        // Get user id by email
        String existingUserId = accountsKeycloakGetUser.getUserByEmail(
            accountsUpdatePasswordLinkCommand.email()
        );

        // Null verification
        if (existingUserId == null) { return; }

        // ###### Save token with email in cache

        // ##### Create URL with token

        // ##### Send email with URL to the user

        System.out.println("#################################################");
        System.out.println( generatedToken );
        System.out.println("#################################################");

    }

}