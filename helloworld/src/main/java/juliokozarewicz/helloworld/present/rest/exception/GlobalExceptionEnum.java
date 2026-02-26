package juliokozarewicz.helloworld.present.rest.exception;

import juliokozarewicz.helloworld.domain.exception.DomainExceptionEnum;

public enum GlobalExceptionEnum {

    // Rest error
    BAD_REQUEST(400, "BAD_REQUEST", null),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", null),
    UNPROCESSABLE_ENTITY(422, "UNPROCESSABLE_ENTITY", null),

    // Domain errors
    USER_NOT_FOUND(404, "USER_NOT_FOUND", DomainExceptionEnum.USER_NOT_FOUND),
    INVALID_CREDENTIALS(401, "INVALID_CREDENTIALS", DomainExceptionEnum.INVALID_CREDENTIALS);

    public final int statusCode;
    public final String statusMessage;
    public final DomainExceptionEnum domainError;

    GlobalExceptionEnum(
        int statusCode,
        String statusMessage,
        DomainExceptionEnum domainError
    ) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.domainError = domainError;
    }

    public static GlobalExceptionEnum fromDomainError(DomainExceptionEnum domainError) {
        for (GlobalExceptionEnum e : values()) {
            if (e.domainError == domainError) return e;
        }
        return INTERNAL_SERVER_ERROR;
    }

}