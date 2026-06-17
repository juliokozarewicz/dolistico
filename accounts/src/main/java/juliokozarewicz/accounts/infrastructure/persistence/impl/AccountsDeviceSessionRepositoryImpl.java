package juliokozarewicz.accounts.infrastructure.persistence.impl;

import juliokozarewicz.accounts.domain.entity.AccountsDeviceSessionEntity;
import juliokozarewicz.accounts.domain.repository.AccountsDeviceSessionRepository;
import juliokozarewicz.accounts.infrastructure.persistence.jpa.AccountsDeviceSessionRepositoryJPA;
import juliokozarewicz.accounts.infrastructure.persistence.model.AccountsDeviceSessionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountsDeviceSessionRepositoryImpl implements AccountsDeviceSessionRepository {

    // ==================================================== ( constructor init )

    private final AccountsDeviceSessionRepositoryJPA accountsDeviceSessionRepositoryJPA;

    public AccountsDeviceSessionRepositoryImpl(
        AccountsDeviceSessionRepositoryJPA accountsDeviceSessionRepositoryJPA
    ) {
        this.accountsDeviceSessionRepositoryJPA = accountsDeviceSessionRepositoryJPA;
    }

    // ==================================================== ( mapping helpers )

    private AccountsDeviceSessionModel toModel(AccountsDeviceSessionEntity entity) {
        return AccountsDeviceSessionModel.builder()
        .id(entity.getId())
        .idUser(entity.getIdUser())
        .createdAt(entity.getCreatedAt())
        .ipAddress(entity.getIpAddress())
        .location(entity.getLocation())
        .device(entity.getDevice())
        .method(entity.getMethod())
        .build();
    }

    private AccountsDeviceSessionEntity toEntity(AccountsDeviceSessionModel model) {
        return new AccountsDeviceSessionEntity(
        model.getId(),
        model.getIdUser(),
        model.getCreatedAt(),
        model.getIpAddress(),
        model.getLocation(),
        model.getDevice(),
        model.getMethod()
        );
    }

    // ==================================================== ( operations )

    @Override
    public void save(AccountsDeviceSessionEntity entity) {
        accountsDeviceSessionRepositoryJPA.save(toModel(entity));
    }

    @Override
    public Page<AccountsDeviceSessionEntity> findByIdUser(

        UUID idUser,
        Pageable pageable

    ) {

        Instant last31Days = Instant.now().minus(31, ChronoUnit.DAYS);

        return accountsDeviceSessionRepositoryJPA
        .findByIdUserAndCreatedAtGreaterThanEqual(
            idUser,
            last31Days,
            pageable
        )
        .map(this::toEntity);

    }

}