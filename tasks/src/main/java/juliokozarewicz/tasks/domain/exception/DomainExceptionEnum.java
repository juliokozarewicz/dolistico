package juliokozarewicz.tasks.domain.exception;

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
    TASKS_USER_NOT_FOUND,
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