package juliokozarewicz.helloworld.domain.exeption;

public class NotFoundException extends BusinessException {

    public NotFoundException(Long id) {

        super (
            ErrorCode.RESOURCE_NOT_FOUND,
            "HelloWorld with id " + id + " not found"
        );

    }

}