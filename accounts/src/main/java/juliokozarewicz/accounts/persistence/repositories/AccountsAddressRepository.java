package juliokozarewicz.accounts.persistence.repositories;

import juliokozarewicz.accounts.persistence.entities.AccountsAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountsAddressRepository extends

    JpaRepository<AccountsAddressEntity, UUID>

{

    // Get address by user id
    List<AccountsAddressEntity> findByIdUser(UUID idUser);

    // Get address by id and user id
    Optional<AccountsAddressEntity> findByIdAndIdUser(
        UUID addressId, UUID idUser
    );

}
