package juliokozarewicz.helloworld.domain.exeption;

public enum ErrorCode {

    RESOURCE_NOT_FOUND(404),
    BUSINESS_RULE_VIOLATION(422),
    UNEXPECTED_ERROR(500);

    private final int httpStatus;

    ErrorCode(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}