package juliokozarewicz.tasks.domain.exception;

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
    USER_NOT_FOUND,
    // ================================================ ( rest error codes end )

    // ============================================= ( domain error codes init )

    // Tasks,
    INVALID_PRIORITY,
    INVALID_DATE,
    INVALID_DATE_RANGE,
    INVALID_ALLDAY,
    INVALID_REMINDER,
    TASK_NOT_FOUND,
    DUPLICATED_TASK,

    // Categories
    DUPLICATED_CATEGORY;

    // ============================================== ( domain error codes end )

}