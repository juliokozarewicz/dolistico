package juliokozarewicz.tasks.domain.exception;

public class DomainException extends RuntimeException {

    private final DomainExceptionEnum error;

    public DomainException(DomainExceptionEnum error) {
        super(error.name());
        this.error = error;
    }

    public DomainExceptionEnum getError() {
        return error;
    }

}
