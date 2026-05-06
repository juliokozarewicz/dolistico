package juliokozarewicz.accounts.infrastructure.persistence.jpa;


import juliokozarewicz.accounts.infrastructure.persistence.model.AccountsProfileModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AccountsProfileRepositoryJPA extends JpaRepository<AccountsProfileModel, UUID> {
}