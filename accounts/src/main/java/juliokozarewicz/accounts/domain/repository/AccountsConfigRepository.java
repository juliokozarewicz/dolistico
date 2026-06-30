package juliokozarewicz.accounts.domain.repository;

import juliokozarewicz.accounts.domain.entity.AccountsConfigEntity;

import java.util.Optional;
import java.util.UUID;

public interface AccountsConfigRepository {

    void save(AccountsConfigEntity entity);

    Optional<AccountsConfigEntity> findByConfigName(String configName);

    Optional<AccountsConfigEntity> findById(UUID id);

}