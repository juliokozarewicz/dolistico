package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsPofileUpdateCommand;
import juliokozarewicz.accounts.domain.entity.AccountsProfileEntity;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.domain.repository.AccountsProfileRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AccountsProfileUpdateUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------
    private final AccountsProfileRepository accountsProfileRepository;

    public AccountsProfileUpdateUseCase(

        AccountsProfileRepository accountsProfileRepository

    ) {

        this.accountsProfileRepository = accountsProfileRepository;

    }

    // ===================================================== ( constructor end )

    public void execute(

        UUID idUser,
        AccountsPofileUpdateCommand accountsPofileUpdateCommand

    ) {

        // Get profile
        AccountsProfileEntity profile = accountsProfileRepository.findByIdUser(idUser)
        .orElseThrow(() ->
            new DomainException(DomainExceptionEnum.BAD_REQUEST)
        );

        // Update entity
        profile.update(
            accountsPofileUpdateCommand.fullName(),
            accountsPofileUpdateCommand.phone(),
            accountsPofileUpdateCommand.identityDocument(),
            accountsPofileUpdateCommand.gender(),
            accountsPofileUpdateCommand.birthdate(),
            accountsPofileUpdateCommand.biography(),
            accountsPofileUpdateCommand.language(),
            accountsPofileUpdateCommand.theme()
        );

        // Persist changes
        accountsProfileRepository.save(profile);

    }

}