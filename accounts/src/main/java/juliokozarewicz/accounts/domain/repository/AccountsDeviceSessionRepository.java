package juliokozarewicz.accounts.domain.repository;

import juliokozarewicz.accounts.domain.entity.AccountsDeviceSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface AccountsDeviceSessionRepository {

    void save(AccountsDeviceSessionEntity entity);
    Page<AccountsDeviceSessionEntity> findByIdUser(
        UUID idUser,
        Pageable pageable
    );

}