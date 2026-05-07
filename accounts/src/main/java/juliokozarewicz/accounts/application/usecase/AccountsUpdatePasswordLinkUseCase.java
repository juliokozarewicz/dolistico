package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsUpdatePasswordLinkCommand;
import org.springframework.stereotype.Service;

@Service
public class AccountsUpdatePasswordLinkUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    public AccountsUpdatePasswordLinkUseCase(

    ) {

    }

    // ===================================================== ( constructor end )

    public void execute(

        AccountsUpdatePasswordLinkCommand accountsUpdatePasswordLinkCommand

    ) {

        System.out.println("Running...");

    }

}