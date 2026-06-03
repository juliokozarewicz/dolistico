package juliokozarewicz.accounts.infrastructure.persistence.impl;

import juliokozarewicz.accounts.application.command.AccountsCreateLogCommand;
import juliokozarewicz.accounts.domain.repository.AccountsEventLogRepository;
import juliokozarewicz.accounts.infrastructure.persistence.jpa.AccountsEventLogRepositoryJPA;
import juliokozarewicz.accounts.infrastructure.persistence.model.AccountsEventLogModel;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public class AccountsEventLogRepositoryImpl implements AccountsEventLogRepository {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsEventLogRepositoryJPA accountsEventLogRepositoryJPA;

    public AccountsEventLogRepositoryImpl(

        AccountsEventLogRepositoryJPA accountsEventLogRepositoryJPA

    ) {

        this.accountsEventLogRepositoryJPA = accountsEventLogRepositoryJPA;

    }

    // ===================================================== ( constructor end )

    @Override
    public void save(

        AccountsCreateLogCommand accountsCreateLogCommand

    ) {

        AccountsEventLogModel model =
            AccountsEventLogModel.builder()
            .id(UUID.randomUUID())
            .idUser(UUID.fromString(accountsCreateLogCommand.idUser()))
            .ipAddress(accountsCreateLogCommand.ipAddress())
            .agent(accountsCreateLogCommand.agent())
            .updateType(accountsCreateLogCommand.updateType())
            .createdAt(Instant.now())
            .oldValue(accountsCreateLogCommand.oldValue())
            .newValue(accountsCreateLogCommand.newValue())
            .build();

        accountsEventLogRepositoryJPA.save(model);

    }

}