package juliokozarewicz.helloworld.adapter.rest.enums;

import juliokozarewicz.helloworld.domain.exception.DomainExceptionEnum;

public enum GlobalExceptionEnum {

    // Domain error code
    INVALID_CREDENTIALS(401, "INVALID_CREDENTIALS", DomainExceptionEnum.INVALID_CREDENTIALS),
    PAYMENT_REQUIRED(402, "PAYMENT_REQUIRED", DomainExceptionEnum.PAYMENT_REQUIRED),
    NO_PERMISSION_TO_ACCESS(403, "NO_PERMISSION_TO_ACCESS", DomainExceptionEnum.NO_PERMISSION_TO_ACCESS),
    USER_NOT_FOUND(404, "USER_NOT_FOUND", DomainExceptionEnum.USER_NOT_FOUND),

    // Rest error code
    SERVICE_UNAVAILABLE(503, "SERVICE_UNAVAILABLE", null),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", null),
    TOO_MANY_REQUESTS(429, "TOO_MANY_REQUESTS", null),
    UNPROCESSABLE_ENTITY(422, "UNPROCESSABLE_ENTITY", null),
    ACCESS_EXPIRED(401, "ACCESS_EXPIRED", null),
    BAD_REQUEST(400, "BAD_REQUEST", null);

    // DTO error code
    public static final String FIELD_CANNOT_REMAIN_EMPTY = "FIELD_CANNOT_REMAIN_EMPTY";
    public static final String MANY_CHARACTERS = "MANY_CHARACTERS";
    public static final String FORBIDDEN_CHARACTERS = "FORBIDDEN_CHARACTERS";

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