package juliokozarewicz.accounts.domain.exception;

public enum DomainExceptionEnum {

    // =============================================== ( rest error codes init )
    SERVICE_UNAVAILABLE,
    INTERNAL_SERVER_ERROR,
    TOO_MANY_REQUESTS,
    UNPROCESSABLE_ENTITY,
    NO_PERMISSION_TO_ACCESS,
    PAYMENT_REQUIRED,
    ACCESS_EXPIRED,
    INVALID_CREDENTIALS,
    BAD_REQUEST,
    FORBIDDEN_CHARACTERS,
    INTERNAL_INSTABILITY,
    // ================================================ ( rest error codes end )

    // ============================================= ( domain error codes init )

    // accounts
    ACCOUNTS_USER_NOT_FOUND,

    // ============================================== ( domain error codes end )

}