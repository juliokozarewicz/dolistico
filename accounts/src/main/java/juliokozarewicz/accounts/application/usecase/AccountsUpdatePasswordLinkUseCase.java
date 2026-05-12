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

        System.out.println("#################################################");
        System.out.println( generatedToken );
        System.out.println("#################################################");

    }

}