package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.domain.entity.AccountsEntity;
import org.springframework.stereotype.Service;

@Service
public class AccountsCreateUseCase {

    public String execute(String message) {

        AccountsEntity finalMessage = new AccountsEntity(message);
        return finalMessage.getMessage();

    }

}