package juliokozarewicz.accounts.adapter.rest.enums;

import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;

public enum GlobalExceptionEnum {

    // =============================================== ( rest error codes init )
    SERVICE_UNAVAILABLE(503, "SERVICE_UNAVAILABLE", null),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", null),
    TOO_MANY_REQUESTS(429, "TOO_MANY_REQUESTS", null),
    UNPROCESSABLE_ENTITY(422, "UNPROCESSABLE_ENTITY", null),
    NO_PERMISSION_TO_ACCESS(403, "NO_PERMISSION_TO_ACCESS", DomainExceptionEnum.NO_PERMISSION_TO_ACCESS),
    PAYMENT_REQUIRED(402, "PAYMENT_REQUIRED", DomainExceptionEnum.PAYMENT_REQUIRED),
    ACCESS_EXPIRED(401, "ACCESS_EXPIRED", null),
    INVALID_CREDENTIALS(401, "INVALID_CREDENTIALS", DomainExceptionEnum.INVALID_CREDENTIALS),
    BAD_REQUEST(400, "BAD_REQUEST", null),
    // ================================================ ( rest error codes end )

    // ============================================= ( domain error codes init )

    // accounts
    ACCOUNTS_USER_NOT_FOUND(404, "ACCOUNTS_USER_NOT_FOUND", DomainExceptionEnum.ACCOUNTS_USER_NOT_FOUND);

    // ============================================== ( domain error codes end )

    // ================================================ ( dto error codes init )
    public static final String ACCOUNTS_FIELD_CANNOT_BE_EMPTY_DTO = "ACCOUNTS_FIELD_CANNOT_BE_EMPTY_DTO";
    public static final String ACCOUNTS_TOO_MANY_CHARACTERS_DTO = "ACCOUNTS_TOO_MANY_CHARACTERS_DTO";
    public static final String ACCOUNTS_TOO_FEW_CHARACTERS_DTO = "ACCOUNTS_TOO_FEW_CHARACTERS_DTO";
    public static final String ACCOUNTS_FORBIDDEN_CHARACTERS_DTO = "ACCOUNTS_FORBIDDEN_CHARACTERS_DTO";
    public static final String ACCOUNTS_INVALID_ID_DTO = "ACCOUNTS_INVALID_ID_DTO";
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