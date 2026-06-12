package juliokozarewicz.accounts.adapter.rest.enums;

import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;

public enum GlobalExceptionEnum {

    // =============================================== ( rest error codes init )
    SERVICE_UNAVAILABLE(503, "SERVICE_UNAVAILABLE", DomainExceptionEnum.SERVICE_UNAVAILABLE),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", DomainExceptionEnum.INTERNAL_SERVER_ERROR),
    INTERNAL_INSTABILITY(500, "INTERNAL_INSTABILITY", DomainExceptionEnum.INTERNAL_INSTABILITY),
    TOO_MANY_REQUESTS(429, "TOO_MANY_REQUESTS", DomainExceptionEnum.TOO_MANY_REQUESTS),
    UNPROCESSABLE_ENTITY(422, "UNPROCESSABLE_ENTITY", DomainExceptionEnum.UNPROCESSABLE_ENTITY),
    NO_PERMISSION_TO_ACCESS(403, "NO_PERMISSION_TO_ACCESS", DomainExceptionEnum.NO_PERMISSION_TO_ACCESS),
    PAYMENT_REQUIRED(402, "PAYMENT_REQUIRED", DomainExceptionEnum.PAYMENT_REQUIRED),
    ACCESS_EXPIRED(401, "ACCESS_EXPIRED", DomainExceptionEnum.ACCESS_EXPIRED),
    INVALID_CREDENTIALS(401, "INVALID_CREDENTIALS", DomainExceptionEnum.INVALID_CREDENTIALS),
    BAD_REQUEST(400, "BAD_REQUEST", DomainExceptionEnum.BAD_REQUEST),
    BUSINESS_RULES_VIOLATION(400, "BUSINESS_RULES_VIOLATION", DomainExceptionEnum.BUSINESS_RULES_VIOLATION),
    // ================================================ ( rest error codes end )

    // ============================================= ( domain error codes init )

    // accounts
    ACCOUNTS_USER_ALREADY_EXISTS(409, "ACCOUNTS_USER_ALREADY_EXISTS", DomainExceptionEnum.ACCOUNTS_USER_ALREADY_EXISTS),
    ACCOUNTS_EXPIRED_LINK(404, "ACCOUNTS_EXPIRED_LINK", DomainExceptionEnum.ACCOUNTS_EXPIRED_LINK),
    ACCOUNTS_USER_NOT_FOUND(404, "ACCOUNTS_USER_NOT_FOUND", DomainExceptionEnum.ACCOUNTS_USER_NOT_FOUND),
    ACCOUNTS_INVALID_PIN(404, "ACCOUNTS_INVALID_PIN", DomainExceptionEnum.ACCOUNTS_INVALID_PIN),
    ACCOUNTS_UPLOAD_AVATAR_ERROR(400, "ACCOUNTS_UPLOAD_AVATAR_ERROR", DomainExceptionEnum.ACCOUNTS_UPLOAD_AVATAR_ERROR),
    ACCOUNTS_AVATAR_ONLY_ONE_IMAGE_ERROR(400, "ACCOUNTS_AVATAR_ONLY_ONE_IMAGE_ERROR", DomainExceptionEnum.ACCOUNTS_AVATAR_ONLY_ONE_IMAGE_ERROR),
    ACCOUNTS_AVATAR_ONLY_IMAGE_ERROR(400, "ACCOUNTS_AVATAR_ONLY_IMAGE_ERROR", DomainExceptionEnum.ACCOUNTS_AVATAR_ONLY_IMAGE_ERROR),
    ACCOUNTS_AVATAR_IMAGE_TOO_LARGE_ERROR(400, "ACCOUNTS_AVATAR_IMAGE_TOO_LARGE_ERROR", DomainExceptionEnum.ACCOUNTS_AVATAR_IMAGE_TOO_LARGE_ERROR);

    // ============================================== ( domain error codes end )

    // ================================================ ( dto error codes init )
    public static final String ACCOUNTS_FIELD_CANNOT_BE_EMPTY_DTO = "ACCOUNTS_FIELD_CANNOT_BE_EMPTY_DTO";
    public static final String ACCOUNTS_INVALID_EMAIL_DTO = "ACCOUNTS_INVALID_EMAIL_DTO";
    public static final String ACCOUNTS_TOO_MANY_CHARACTERS_DTO = "ACCOUNTS_TOO_MANY_CHARACTERS_DTO";
    public static final String ACCOUNTS_TOO_FEW_CHARACTERS_DTO = "ACCOUNTS_TOO_FEW_CHARACTERS_DTO";
    public static final String ACCOUNTS_FORBIDDEN_CHARACTERS_DTO = "ACCOUNTS_FORBIDDEN_CHARACTERS_DTO";
    public static final String ACCOUNTS_PASSWORD_REQUIREMENTS_DTO = "ACCOUNTS_PASSWORD_REQUIREMENTS_DTO";
    public static final String ACCOUNTS_INVALID_ID_DTO = "ACCOUNTS_INVALID_ID_DTO";
    public static final String ACCOUNTS_INVALID_TOKEN_DTO = "ACCOUNTS_INVALID_TOKEN_DTO";
    public static final String ACCOUNTS_INVALID_PIN_DTO = "ACCOUNTS_INVALID_PIN_DTO";
    public static final String ACCOUNTS_INVALID_DATE_DTO = "ACCOUNTS_INVALID_DATE_DTO";
    // ================================================= ( dto error codes end )

    public final int statusCode;
    public final String messageCode;
    public final DomainExceptionEnum domainError;

    GlobalExceptionEnum(
        int statusCode,
        String messageCode,
        DomainExceptionEnum domainError
    ) {
        this.statusCode = statusCode;
        this.messageCode = messageCode;
        this.domainError = domainError;
    }

    public static GlobalExceptionEnum fromDomainError(DomainExceptionEnum domainError) {
        for (GlobalExceptionEnum e : values()) {
            if (e.domainError == domainError) return e;
        }
        return INTERNAL_SERVER_ERROR;
    }

}