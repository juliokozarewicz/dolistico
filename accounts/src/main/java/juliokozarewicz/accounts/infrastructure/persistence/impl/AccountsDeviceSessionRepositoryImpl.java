package juliokozarewicz.accounts.infrastructure.persistence.impl;

import juliokozarewicz.accounts.domain.entity.AccountsDeviceSessionEntity;
import juliokozarewicz.accounts.domain.repository.AccountsDeviceSessionRepository;
import juliokozarewicz.accounts.infrastructure.persistence.jpa.AccountsDeviceSessionRepositoryJPA;
import juliokozarewicz.accounts.infrastructure.persistence.model.AccountsDeviceSessionModel;
import org.springframework.stereotype.Repository;

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
    public void delete(String id) {
        accountsDeviceSessionRepositoryJPA.deleteById(UUID.fromString(id));
    }

    @Override
    public Optional<AccountsDeviceSessionEntity> findById(UUID id) {
        return accountsDeviceSessionRepositoryJPA.findById(id).map(this::toEntity);
    }
}