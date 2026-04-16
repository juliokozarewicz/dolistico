package juliokozarewicz.helloworld.domain.exception;

public enum DomainExceptionEnum {

    // =============================================== ( rest error codes init )
    TASKS_SERVICE_UNAVAILABLE,
    TASKS_INTERNAL_SERVER_ERROR,
    TASKS_TOO_MANY_REQUESTS,
    TASKS_UNPROCESSABLE_ENTITY,
    TASKS_NO_PERMISSION_TO_ACCESS,
    TASKS_PAYMENT_REQUIRED,
    TASKS_ACCESS_EXPIRED,
    TASKS_INVALID_CREDENTIALS,
    TASKS_BAD_REQUEST,
    TASKS_FIELD_CANNOT_BE_EMPTY,
    TASKS_TOO_MANY_CHARACTERS,
    TASKS_FORBIDDEN_CHARACTERS,
    // ================================================ ( rest error codes end )

    // ============================================= ( domain error codes init )

    // hello world
    TASKS_USER_NOT_FOUND

    // ============================================== ( domain error codes end )

}