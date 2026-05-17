package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsUpdatePasswordLinkCommand;
import juliokozarewicz.accounts.infrastructure.security.TokenGenerator;
import org.springframework.stereotype.Service;

@Service
public class AccountsUpdatePasswordLinkUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final TokenGenerator tokenGenerator;

    public AccountsUpdatePasswordLinkUseCase(

        TokenGenerator tokenGenerator

    ) {

        this.tokenGenerator = tokenGenerator;

    }

    // ===================================================== ( constructor end )

    public void execute(

        AccountsUpdatePasswordLinkCommand accountsUpdatePasswordLinkCommand

    ) {

        // Generate token for verification
        String generatedToken = tokenGenerator.generate512Hex();

        // ##### Get user id by email

        // ##### Null verification

        // ###### Save token with email in cache

        // ##### Create URL with token

        // ##### Send email with URL to the user

        System.out.println("#################################################");
        System.out.println( generatedToken );
        System.out.println("#################################################");

    }

}