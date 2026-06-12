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

    // Validation patterns
    // -------------------------------------------------------------------------
    private static final Pattern FORBIDDEN_CHARS =
        Pattern.compile("^[^<>&'\"/]*$");

    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^[+\\d()\\s-]*$");

    private static final Pattern DATE_PATTERN =
        Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");

    private static final Pattern LANGUAGE_THEME_PATTERN =
        Pattern.compile("^[a-zA-Z-]*$");

    // Entity fields
    // -------------------------------------------------------------------------

    private UUID idUser;
    private Instant createdAt;
    private Instant updatedAt;
    private String avatar;
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
        String avatar,
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
        this.avatar = avatar;
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

    // ========================================================= ( update init )

    // Updates profile information and validates all business rules
    public void update(

        String fullName,
        String phone,
        String identityDocument,
        String gender,
        String birthdate,
        String biography,
        String language,
        String theme

    ) {

        if (fullName != null) {
            validateFullName(fullName);
            this.fullName = fullName;
        }

        if (phone != null) {
            validatePhone(phone);
            this.phone = phone;
        }

        if (identityDocument != null) {
            validateIdentityDocument(identityDocument);
            this.identityDocument = identityDocument;
        }

        if (gender != null) {
            validateGender(gender);
            this.gender = gender;
        }

        if (birthdate != null) {
            validateBirthdate(birthdate);
            this.birthdate = birthdate;
        }

        if (biography != null) {
            validateBiography(biography);
            this.biography = biography;
        }

        if (language != null) {
            validateLanguage(language);
            this.language = language;
        }

        if (theme != null) {
            validateTheme(theme);
            this.theme = theme;
        }

        this.updatedAt = Instant.now();

    }

    // ========================================================== ( update end )

    // ===================================================== ( validation init )

    // Validates entity business rules
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

        validateFullName(fullName);
        validatePhone(phone);
        validateIdentityDocument(identityDocument);
        validateGender(gender);
        validateBirthdate(birthdate);
        validateBiography(biography);
        validateLanguage(language);
        validateTheme(theme);

    }

    // Validates full name
    private void validateFullName(String value) {

        if (value == null) {
            return;
        }

        if (value.isBlank()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (value.length() < 3) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (value.length() > 256) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (!FORBIDDEN_CHARS.matcher(value).matches()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

    }

    // Validates phone number
    private void validatePhone(String value) {

        if (value == null) {
            return;
        }

        if (value.isBlank()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (value.length() < 1) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (value.length() > 25) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (!PHONE_PATTERN.matcher(value).matches()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

    }

    // Validates identity document
    private void validateIdentityDocument(String value) {

        if (value == null) {
            return;
        }

        if (value.isBlank()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (value.length() < 1) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (value.length() > 256) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (!FORBIDDEN_CHARS.matcher(value).matches()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

    }

    // Validates gender
    private void validateGender(String value) {

        if (value == null) {
            return;
        }

        if (value.isBlank()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (value.length() < 1) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (value.length() > 256) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (!FORBIDDEN_CHARS.matcher(value).matches()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

    }

    // Validates birthdate format
    private void validateBirthdate(String value) {

        if (value == null) {
            return;
        }

        if (value.isBlank()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (value.length() < 1) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (!DATE_PATTERN.matcher(value).matches()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

    }

    // Validates biography
    private void validateBiography(String value) {

        if (value == null) {
            return;
        }

        if (value.isBlank()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (value.length() < 1) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (value.length() > 256) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (!FORBIDDEN_CHARS.matcher(value).matches()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

    }

    // Validates language
    private void validateLanguage(String value) {

        if (value == null) {
            return;
        }

        if (value.isBlank()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (value.length() < 1) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (value.length() > 50) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (!LANGUAGE_THEME_PATTERN.matcher(value).matches()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

    }

    // Validates theme
    private void validateTheme(String value) {

        if (value == null) {
            return;
        }

        if (value.isBlank()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (value.length() < 1) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (value.length() > 100) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (!LANGUAGE_THEME_PATTERN.matcher(value).matches()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

    }

    // Remove avatar
    public void removeAvatar() {
        this.avatar = null;
        this.updatedAt = Instant.now();
    }

    // Update avatar
    public void updateAvatar(String avatar) {
        this.avatar = avatar;
        this.updatedAt = Instant.now();
    }

    // ====================================================== ( validation end )

}