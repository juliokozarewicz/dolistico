package juliokozarewicz.accounts.infrastructure.persistence.jpa;


import juliokozarewicz.accounts.infrastructure.persistence.model.AccountsDeviceSessionModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountsDeviceSessionRepositoryJPA extends JpaRepository<AccountsDeviceSessionModel, UUID> {
}