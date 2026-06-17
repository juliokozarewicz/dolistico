package juliokozarewicz.accounts.domain.repository;

import juliokozarewicz.accounts.domain.entity.AccountsDeviceSessionEntity;

import java.util.Optional;
import java.util.UUID;

public interface AccountsDeviceSessionRepository {

    void save(AccountsDeviceSessionEntity entity);

    void delete(String id);

    Optional<AccountsDeviceSessionEntity> findById(UUID id);
}