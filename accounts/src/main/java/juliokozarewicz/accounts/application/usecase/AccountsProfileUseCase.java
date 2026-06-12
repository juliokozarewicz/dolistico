package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.domain.entity.AccountsProfileEntity;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.domain.repository.AccountsProfileRepository;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUser;
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
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;

    public AccountsProfileUseCase(

        AccountsProfileRepository accountsProfileRepository,
        AccountsKeycloakGetUser accountsKeycloakGetUser

    ) {

        this.accountsProfileRepository = accountsProfileRepository;
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;

    }

    // ===================================================== ( constructor end )

    public Map<String, Object> execute(

        UUID idUser

    ) {

        // get user data
        Map<String, Object> user = accountsKeycloakGetUser.getUserById(idUser.toString());

        // Extract email
        String userEmail = user != null ? (String) user.get("email") : null;

        // Email verify
        if (userEmail == null) {
            throw new DomainException(DomainExceptionEnum.BAD_REQUEST);
        }

        // Get profile
        AccountsProfileEntity profile = accountsProfileRepository.findByIdUser(idUser)
        .orElseThrow(() ->
            new DomainException(DomainExceptionEnum.BAD_REQUEST)
        );

        // Return
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("createdAt", profile.getCreatedAt());
        response.put("updatedAt", profile.getUpdatedAt());
        response.put("avatar", profile.getAvatar());
        response.put("email", userEmail);
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