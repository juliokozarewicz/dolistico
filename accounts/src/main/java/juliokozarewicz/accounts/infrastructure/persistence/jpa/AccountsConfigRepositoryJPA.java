package juliokozarewicz.accounts.infrastructure.persistence.jpa;

import juliokozarewicz.accounts.infrastructure.persistence.model.AccountsConfigModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountsConfigRepositoryJPA extends JpaRepository<AccountsConfigModel, UUID> {

    Optional<AccountsConfigModel> findByConfigName(String configName);

}