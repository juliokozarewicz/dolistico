package juliokozarewicz.emailservice.adapter.rest.enums;

import juliokozarewicz.emailservice.domain.exception.DomainExceptionEnum;

public enum GlobalExceptionEnum {

    // =============================================== ( rest error codes init )
    SERVICE_UNAVAILABLE(503, "SERVICE_UNAVAILABLE", DomainExceptionEnum.SERVICE_UNAVAILABLE),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", DomainExceptionEnum.INTERNAL_SERVER_ERROR),
    TOO_MANY_REQUESTS(429, "TOO_MANY_REQUESTS", DomainExceptionEnum.TOO_MANY_REQUESTS),
    UNPROCESSABLE_ENTITY(422, "UNPROCESSABLE_ENTITY", DomainExceptionEnum.UNPROCESSABLE_ENTITY),
    NO_PERMISSION_TO_ACCESS(403, "NO_PERMISSION_TO_ACCESS", DomainExceptionEnum.NO_PERMISSION_TO_ACCESS),
    PAYMENT_REQUIRED(402, "PAYMENT_REQUIRED", DomainExceptionEnum.PAYMENT_REQUIRED),
    ACCESS_EXPIRED(401, "ACCESS_EXPIRED", DomainExceptionEnum.ACCESS_EXPIRED),
    INVALID_CREDENTIALS(401, "INVALID_CREDENTIALS", DomainExceptionEnum.INVALID_CREDENTIALS),
    BAD_REQUEST(400, "BAD_REQUEST", DomainExceptionEnum.BAD_REQUEST),
    BUSINESS_RULES_VIOLATION(400, "BUSINESS_RULES_VIOLATION", DomainExceptionEnum.BUSINESS_RULES_VIOLATION),
    INTERNAL_INSTABILITY(500, "INTERNAL_INSTABILITY", DomainExceptionEnum.INTERNAL_INSTABILITY);
    // ================================================ ( rest error codes end )

    // ============================================= ( domain error codes init )
    // ============================================== ( domain error codes end )

    // ================================================ ( dto error codes init )
    public static final String HELLOWORLD_FIELD_CANNOT_BE_EMPTY_DTO = "HELLOWORLD_FIELD_CANNOT_BE_EMPTY_DTO";
    public static final String HELLOWORLD_TOO_MANY_CHARACTERS_DTO = "HELLOWORLD_TOO_MANY_CHARACTERS_DTO";
    public static final String HELLOWORLD_TOO_FEW_CHARACTERS_DTO = "HELLOWORLD_TOO_FEW_CHARACTERS_DTO";
    public static final String HELLOWORLD_FORBIDDEN_CHARACTERS_DTO = "HELLOWORLD_FORBIDDEN_CHARACTERS_DTO";
    public static final String HELLOWORLD_INVALID_ID_DTO = "HELLOWORLD_INVALID_ID_DTO";
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
        return INTERNAL_SERVER_ERROR;
    }

}