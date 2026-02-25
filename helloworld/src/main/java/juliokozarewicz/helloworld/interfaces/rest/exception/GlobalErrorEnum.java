package juliokozarewicz.helloworld.interfaces.rest.exception;

import juliokozarewicz.helloworld.domain.exception.DomainErrorEnum;

public enum GlobalErrorEnum {

    // Rest error
    BAD_REQUEST(400, "BAD_REQUEST", null),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", null),

    // Domain errors
    USER_NOT_FOUND(404, "USER_NOT_FOUND", DomainErrorEnum.USER_NOT_FOUND),
    INVALID_CREDENTIALS(401, "INVALID_CREDENTIALS", DomainErrorEnum.INVALID_CREDENTIALS);

    public final int statusCode;
    public final String statusMessage;
    public final DomainErrorEnum domainError;

    GlobalErrorEnum(
        int statusCode,
        String statusMessage,
        DomainErrorEnum domainError
    ) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.domainError = domainError;
    }

    public static GlobalErrorEnum fromDomainError(DomainErrorEnum domainError) {
        for (GlobalErrorEnum e : values()) {
            if (e.domainError == domainError) return e;
        }
        return INTERNAL_SERVER_ERROR;
    }

}