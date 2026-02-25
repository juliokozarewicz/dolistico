package juliokozarewicz.helloworld.domain.exception;

public class DomainException extends RuntimeException {

    private final DomainErrorEnum error;

    public DomainException(DomainErrorEnum error) {
        super(error.name());
        this.error = error;
    }

    public DomainErrorEnum getError() {
        return error;
    }

}
