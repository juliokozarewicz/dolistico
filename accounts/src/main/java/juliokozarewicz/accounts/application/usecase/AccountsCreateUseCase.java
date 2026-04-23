package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsCreateCommand;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AccountsCreateUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsKeycloakTokenProvider tokenProvider;

    public AccountsCreateUseCase(

        AccountsKeycloakTokenProvider tokenProvider

    ) {

        this.tokenProvider = tokenProvider;

    }

    // ===================================================== ( constructor end )

    public String execute(

        AccountsCreateCommand command

    ) {

        String name = command.fullName() == null ? "" : command.fullName().trim();

        String token = tokenProvider.getAccessToken();

        System.out.println("Received token: " + token);

        return "Account received for " + (name.isEmpty() ? "<unknown>" : name);

    }

}