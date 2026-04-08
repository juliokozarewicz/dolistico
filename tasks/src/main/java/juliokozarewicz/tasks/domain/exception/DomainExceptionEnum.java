package juliokozarewicz.tasks.domain.exception;

public enum DomainExceptionEnum {

    // Initial error codes
    INVALID_CREDENTIALS,
    PAYMENT_REQUIRED,
    NO_PERMISSION_TO_ACCESS,
    USER_NOT_FOUND,

    // Tasks error codes
    INVALID_PRIORITY,
    INVALID_DATE,
    INVALID_DATE_RANGE,
    INVALID_ALLDAY,
    INVALID_REMINDER,
    TASK_NOT_FOUND,
    DUPLICATED_TASK,
    FIELD_CANNOT_BE_EMPTY,
    TOO_MANY_CHARACTERS,
    FORBIDDEN_CHARACTERS,

    // Category error codes
    DUPLICATED_CATEGORY;

}