package juliokozarewicz.helloworld.adapter.rest.enums;

import juliokozarewicz.helloworld.domain.exception.DomainExceptionEnum;

public enum GlobalExceptionEnum {

    // ============================================= ( domain error codes init )

    // hello world
    TASKS_USER_NOT_FOUND(404, "TASKS_USER_NOT_FOUND", DomainExceptionEnum.TASKS_USER_NOT_FOUND),
    TASKS_FIELD_CANNOT_BE_EMPTY(400, "TASKS_FIELD_CANNOT_BE_EMPTY", DomainExceptionEnum.TASKS_FIELD_CANNOT_BE_EMPTY),
    TASKS_TOO_MANY_CHARACTERS(400, "TASKS_TOO_MANY_CHARACTERS", DomainExceptionEnum.TASKS_TOO_MANY_CHARACTERS),
    TASKS_FORBIDDEN_CHARACTERS(400, "TASKS_FORBIDDEN_CHARACTERS", DomainExceptionEnum.TASKS_FORBIDDEN_CHARACTERS),

    // ============================================== ( domain error codes end )

    // =============================================== ( rest error codes init )
    TASKS_SERVICE_UNAVAILABLE(503, "TASKS_SERVICE_UNAVAILABLE", null),
    TASKS_INTERNAL_SERVER_ERROR(500, "TASKS_INTERNAL_SERVER_ERROR", null),
    TASKS_TOO_MANY_REQUESTS(429, "TASKS_TOO_MANY_REQUESTS", null),
    TASKS_UNPROCESSABLE_ENTITY(422, "TASKS_UNPROCESSABLE_ENTITY", null),
    TASKS_NO_PERMISSION_TO_ACCESS(403, "TASKS_NO_PERMISSION_TO_ACCESS", DomainExceptionEnum.TASKS_NO_PERMISSION_TO_ACCESS),
    TASKS_PAYMENT_REQUIRED(402, "TASKS_PAYMENT_REQUIRED", DomainExceptionEnum.TASKS_PAYMENT_REQUIRED),
    TASKS_ACCESS_EXPIRED(401, "TASKS_ACCESS_EXPIRED", null),
    TASKS_INVALID_CREDENTIALS(401, "TASKS_INVALID_CREDENTIALS", DomainExceptionEnum.TASKS_INVALID_CREDENTIALS),
    TASKS_BAD_REQUEST(400, "TASKS_BAD_REQUEST", null);
    // ================================================ ( rest error codes end )

    // ================================================ ( dto error codes init )
    public static final String TASKS_FIELD_CANNOT_BE_EMPTY_DTO = "TASKS_FIELD_CANNOT_BE_EMPTY_DTO";
    public static final String TASKS_TOO_MANY_CHARACTERS_DTO = "TASKS_TOO_MANY_CHARACTERS_DTO";
    public static final String TASKS_INVALID_PRIORITY_DTO = "TASKS_INVALID_PRIORITY";
    public static final String TASKS_TOO_FEW_CHARACTERS_DTO = "TASKS_TOO_FEW_CHARACTERS_DTO";
    public static final String TASKS_FORBIDDEN_CHARACTERS_DTO = "TASKS_FORBIDDEN_CHARACTERS_DTO";
    public static final String TASKS_INVALID_ID_DTO = "TASKS_INVALID_ID_DTO";
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
        return TASKS_INTERNAL_SERVER_ERROR;
    }

}