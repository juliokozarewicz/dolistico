package juliokozarewicz.tasks.adapter.rest.enums;

import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;

public enum GlobalExceptionEnum {

    // Domain error codes
    INVALID_PRIORITY(400, "INVALID_PRIORITY", DomainExceptionEnum.INVALID_PRIORITY),
    USER_NOT_FOUND(404, "USER_NOT_FOUND", DomainExceptionEnum.USER_NOT_FOUND),
    INVALID_DATE(400, "INVALID_DATE", DomainExceptionEnum.INVALID_DATE),
    INVALID_DATE_RANGE(400, "INVALID_DATE_RANGE", DomainExceptionEnum.INVALID_DATE_RANGE),
    INVALID_ALLDAY(400, "INVALID_ALLDAY", DomainExceptionEnum.INVALID_ALLDAY),
    INVALID_REMINDER(400, "INVALID_REMINDER", DomainExceptionEnum.INVALID_REMINDER),
    DUPLICATED_TASK(409, "DUPLICATED_TASK", DomainExceptionEnum.DUPLICATED_TASK),
    TASK_NOT_FOUND(404, "TASK_NOT_FOUND", DomainExceptionEnum.TASK_NOT_FOUND),
    FIELD_CANNOT_BE_EMPTY(400, "FIELD_CANNOT_BE_EMPTY", DomainExceptionEnum.FIELD_CANNOT_BE_EMPTY),
    TOO_MANY_CHARACTERS(400, "TOO_MANY_CHARACTERS", DomainExceptionEnum.TOO_MANY_CHARACTERS),
    FORBIDDEN_CHARACTERS(400, "FORBIDDEN_CHARACTERS", DomainExceptionEnum.FORBIDDEN_CHARACTERS),

    // Rest error codes
    SERVICE_UNAVAILABLE(503, "SERVICE_UNAVAILABLE", null),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", null),
    TOO_MANY_REQUESTS(429, "TOO_MANY_REQUESTS", null),
    UNPROCESSABLE_ENTITY(422, "UNPROCESSABLE_ENTITY", null),
    NO_PERMISSION_TO_ACCESS(403, "NO_PERMISSION_TO_ACCESS", DomainExceptionEnum.NO_PERMISSION_TO_ACCESS),
    PAYMENT_REQUIRED(402, "PAYMENT_REQUIRED", DomainExceptionEnum.PAYMENT_REQUIRED),
    ACCESS_EXPIRED(401, "ACCESS_EXPIRED", null),
    INVALID_CREDENTIALS(401, "INVALID_CREDENTIALS", DomainExceptionEnum.INVALID_CREDENTIALS),
    BAD_REQUEST(400, "BAD_REQUEST", null);

    // DTO error codes
    public static final String FIELD_CANNOT_BE_EMPTY_DTO = "FIELD_CANNOT_BE_EMPTY_DTO";
    public static final String TOO_MANY_CHARACTERS_DTO = "TOO_MANY_CHARACTERS_DTO";
    public static final String INVALID_PRIORITY_DTO = "INVALID_PRIORITY";
    public static final String TOO_FEW_CHARACTERS_DTO = "TOO_FEW_CHARACTERS_DTO";
    public static final String FORBIDDEN_CHARACTERS_DTO = "FORBIDDEN_CHARACTERS_DTO";
    public static final String INVALID_ID_DTO = "INVALID_ID_DTO";

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