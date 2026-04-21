package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsCreateCommand;
import org.springframework.stereotype.Service;

@Service
public class AccountsCreateUseCase {

    public String execute(

        AccountsCreateCommand command

    ) {

        String name = command.fullName() == null ? "" : command.fullName().trim();
        return "Account received for " + (name.isEmpty() ? "<unknown>" : name);

    }

}