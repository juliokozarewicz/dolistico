package juliokozarewicz.accounts.domain.repository;

import juliokozarewicz.accounts.application.command.AccountsCreateLogCommand;

public interface AccountsEventLogRepository {

    void save(AccountsCreateLogCommand accountsCreateLogCommand);

}