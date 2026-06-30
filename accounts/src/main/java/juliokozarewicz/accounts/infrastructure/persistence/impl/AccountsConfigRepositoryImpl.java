package juliokozarewicz.accounts.infrastructure.persistence.impl;

import juliokozarewicz.accounts.domain.entity.AccountsConfigEntity;
import juliokozarewicz.accounts.domain.repository.AccountsConfigRepository;
import juliokozarewicz.accounts.infrastructure.persistence.jpa.AccountsConfigRepositoryJPA;
import juliokozarewicz.accounts.infrastructure.persistence.model.AccountsConfigModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountsConfigRepositoryImpl implements AccountsConfigRepository {

    // ==================================================== ( constructor init )

    private final AccountsConfigRepositoryJPA accountsConfigRepositoryJPA;

    public AccountsConfigRepositoryImpl(
        AccountsConfigRepositoryJPA accountsConfigRepositoryJPA
    ) {
        this.accountsConfigRepositoryJPA = accountsConfigRepositoryJPA;
    }

    // ===================================================== ( constructor end )

    // ======================================================== ( helpers init )

    private AccountsConfigModel toModel(AccountsConfigEntity entity) {
        return AccountsConfigModel.builder()
            .id(entity.getId())
            .configName(entity.getConfigName())
            .configValue(entity.getConfigValue())
            .build();
    }

    private AccountsConfigEntity toEntity(AccountsConfigModel model) {
        return new AccountsConfigEntity(
            model.getId(),
            model.getConfigName(),
            model.getConfigValue()
        );
    }

    // ========================================================= ( helpers end )

    @Override
    public void save(AccountsConfigEntity entity) {
        accountsConfigRepositoryJPA.save(toModel(entity));
    }

    @Override
    public Optional<AccountsConfigEntity> findByConfigName(String configName) {
        return accountsConfigRepositoryJPA.findByConfigName(configName).map(this::toEntity);
    }

    @Override
    public Optional<AccountsConfigEntity> findById(UUID id) {
        return accountsConfigRepositoryJPA.findById(id).map(this::toEntity);
    }

}