package juliokozarewicz.accounts.infrastructure.persistence.jpa;

import juliokozarewicz.accounts.infrastructure.persistence.model.AccountsDeviceSessionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface AccountsDeviceSessionRepositoryJPA
    extends JpaRepository<AccountsDeviceSessionModel, UUID> {

    Page<AccountsDeviceSessionModel>
    findByIdUserAndCreatedAtGreaterThanEqual(
        UUID idUser,
        Instant createdAt,
        Pageable pageable
    );

}