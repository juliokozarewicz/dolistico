package juliokozarewicz.helloworld.domain.usecase;

import juliokozarewicz.helloworld.domain.exception.DomainException;
import juliokozarewicz.helloworld.domain.exception.DomainExceptionEnum;
import org.springframework.stereotype.Service;

@Service
public class HelloWorldUseCase {

    public String execute(String message) {

        String trimmedMessage = message == null ? "" : message.trim();

        String finalMessage = !trimmedMessage.isEmpty() ? trimmedMessage : "Hello World!";

        if (finalMessage.equals("bad-message")) {
            throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
        }

        return finalMessage;
    }

}
