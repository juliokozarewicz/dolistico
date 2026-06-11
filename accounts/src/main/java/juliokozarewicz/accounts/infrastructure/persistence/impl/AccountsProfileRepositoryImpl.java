package juliokozarewicz.accounts.infrastructure.persistence.impl;

import juliokozarewicz.accounts.domain.entity.AccountsProfileEntity;
import juliokozarewicz.accounts.domain.repository.AccountsProfileRepository;
import juliokozarewicz.accounts.infrastructure.persistence.jpa.AccountsProfileRepositoryJPA;
import juliokozarewicz.accounts.infrastructure.persistence.model.AccountsProfileModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountsProfileRepositoryImpl implements AccountsProfileRepository {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsProfileRepositoryJPA accountsProfileRepositoryJPA;

    public AccountsProfileRepositoryImpl(

        AccountsProfileRepositoryJPA accountsProfileRepositoryJPA

    ) {

        this.accountsProfileRepositoryJPA = accountsProfileRepositoryJPA;

    }

    // ===================================================== ( constructor end )

    // ======================================================== ( helpers init )

    // ENTITY -> MODEL
    private AccountsProfileModel toModel(AccountsProfileEntity entity) {
        return AccountsProfileModel.builder()
        .idUser(entity.getIdUser())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .profileImage(entity.getProfileImage())
        .fullName(entity.getFullName())
        .phone(entity.getPhone())
        .identityDocument(entity.getIdentityDocument())
        .gender(entity.getGender())
        .birthdate(entity.getBirthdate())
        .biography(entity.getBiography())
        .language(entity.getLanguage())
        .theme(entity.getTheme())
        .build();
    }

    // MODEL -> ENTITY
    private AccountsProfileEntity toEntity(AccountsProfileModel model) {

        return new AccountsProfileEntity(
            model.getIdUser(),
            model.getCreatedAt(),
            model.getUpdatedAt(),
            model.getProfileImage(),
            model.getFullName(),
            model.getPhone(),
            model.getIdentityDocument(),
            model.getGender(),
            model.getBirthdate(),
            model.getBiography(),
            model.getLanguage(),
            model.getTheme()
        );

    }

    // ========================================================= ( helpers end )

    @Override
    public void save(AccountsProfileEntity accountsProfileEntity) {
        accountsProfileRepositoryJPA.save(toModel(accountsProfileEntity));
    }

    @Override
    public void delete(String idUser) {
        accountsProfileRepositoryJPA.deleteById(UUID.fromString(idUser));
    }

    @Override
    public Optional<AccountsProfileEntity> findByIdUser(UUID idUser) {

        return accountsProfileRepositoryJPA
            .findById(idUser)
            .map(this::toEntity);

    }

}