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
    TASKS_FIELD_CANNOT_BE_EMPTY,
    TASKS_TOO_MANY_CHARACTERS,
    TASKS_FORBIDDEN_CHARACTERS,
    TASKS_USER_NOT_FOUND,
    INTERNAL_INSTABILITY,
    // ================================================ ( rest error codes end )

    // ============================================= ( domain error codes init )

    // Tasks,
    TASKS_INVALID_PRIORITY,
    TASKS_INVALID_DATE,
    TASKS_INVALID_DATE_RANGE,
    TASKS_INVALID_ALLDAY,
    TASKS_INVALID_REMINDER,
    TASKS_NOT_FOUND,
    TASKS_DUPLICATED_TASK,

    // Categories
    TASKS_DUPLICATED_CATEGORY,
    TASKS_CATEGORY_NOT_FOUND,

    // ============================================== ( domain error codes end )

}