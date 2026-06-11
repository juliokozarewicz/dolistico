package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.domain.entity.AccountsProfileEntity;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.domain.repository.AccountsProfileRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AccountsProfileUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------
    private final AccountsProfileRepository accountsProfileRepository;

    public AccountsProfileUseCase(

        AccountsProfileRepository accountsProfileRepository

    ) {

        this.accountsProfileRepository = accountsProfileRepository;

    }

    // ===================================================== ( constructor end )

    public Map<String, Object> execute(

        UUID idUser

    ) {

        // Get profile
        AccountsProfileEntity profile = accountsProfileRepository.findByIdUser(idUser)
        .orElseThrow(() ->
            new DomainException(DomainExceptionEnum.BAD_REQUEST)
        );

        // Return
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("createdAt", profile.getCreatedAt());
        response.put("updatedAt", profile.getUpdatedAt());
        response.put("profileImage", profile.getProfileImage());
        response.put("fullName", profile.getFullName());
        response.put("phone", profile.getPhone());
        response.put("identityDocument", profile.getIdentityDocument());
        response.put("gender", profile.getGender());
        response.put("birthdate", profile.getBirthdate());
        response.put("biography", profile.getBiography());
        response.put("language", profile.getLanguage());
        response.put("theme", profile.getTheme());

        return response;

    }

}