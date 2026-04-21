package juliokozarewicz.accounts.persistence.repositories;

import juliokozarewicz.accounts.persistence.entities.AccountsDeletedEntity;
import juliokozarewicz.accounts.persistence.entities.AccountsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountsDeletedRepository extends

    JpaRepository<AccountsDeletedEntity, UUID>

{

    // Get deleted user by email
    Optional<AccountsEntity> findByEmail(String email);

    // Get deleted user by ID and email
    Optional<AccountsEntity> findByIdAndEmail(UUID id, String email);

}
