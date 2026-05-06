package juliokozarewicz.accounts.domain.entity;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

@Getter
@NoArgsConstructor
public class AccountsProfileEntity {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private static final Pattern FORBIDDEN_CHARS = Pattern.compile("^[^<>&'\"/]*$");

    private UUID idUser;
    private Instant createdAt;
    private Instant updatedAt;
    private String profileImage;
    private String fullName;
    private String phone;
    private String identityDocument;
    private String gender;
    private String birthdate;
    private String biography;
    private String language;
    private String theme;

    public AccountsProfileEntity(

        UUID idUser,
        Instant createdAt,
        Instant updatedAt,
        String profileImage,
        String fullName,
        String phone,
        String identityDocument,
        String gender,
        String birthdate,
        String biography,
        String language,
        String theme

    ) {

        this.idUser = idUser;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.profileImage = profileImage;
        this.fullName = fullName;
        this.phone = phone;
        this.identityDocument = identityDocument;
        this.gender = gender;
        this.birthdate = birthdate;
        this.biography = biography;
        this.language = language;
        this.theme = theme;

        validateBusinessRules();

    }

    // ===================================================== ( constructor end )

    // Bussines rules validation
    private void validateBusinessRules() {

        if (idUser == null) {
            throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
        }

        if (createdAt == null || updatedAt == null) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (updatedAt.isBefore(createdAt)) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (fullName != null) {

            if (fullName.isBlank()) {
                throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
            }

            if (fullName.length() < 3) {
                throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
            }

            if (fullName.length() > 256) {
                throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
            }

            if (!FORBIDDEN_CHARS.matcher(fullName).matches()) {
                throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
            }

        }

    }

}