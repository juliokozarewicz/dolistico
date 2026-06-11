package juliokozarewicz.accounts.domain.repository;

import juliokozarewicz.accounts.domain.entity.AccountsProfileEntity;

import java.util.UUID;
import java.util.Optional;

public interface AccountsProfileRepository {

    void save(AccountsProfileEntity accountsProfileEntity);

    void delete(String idUser);

    Optional<AccountsProfileEntity> findByIdUser(UUID idUser);

}