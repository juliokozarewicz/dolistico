package juliokozarewicz.helloworld.domain.exception;

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
    FIELD_CANNOT_BE_EMPTY,
    TOO_MANY_CHARACTERS,
    FORBIDDEN_CHARACTERS,
    // ================================================ ( rest error codes end )

    // ============================================= ( domain error codes init )

    // hello world
    USER_NOT_FOUND

    // ============================================== ( domain error codes end )

}