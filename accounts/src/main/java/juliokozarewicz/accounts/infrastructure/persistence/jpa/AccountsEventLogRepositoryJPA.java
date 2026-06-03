package juliokozarewicz.accounts.infrastructure.persistence.jpa;

import juliokozarewicz.accounts.infrastructure.persistence.model.AccountsEventLogModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountsEventLogRepositoryJPA extends JpaRepository<AccountsEventLogModel, UUID> {
}