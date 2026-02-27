package juliokozarewicz.helloworld.domain.entity;

import juliokozarewicz.helloworld.domain.exception.DomainException;
import juliokozarewicz.helloworld.domain.exception.DomainExceptionEnum;

public class HelloWorld {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final String messageValidated;

    public HelloWorld (

        String messageValidated

    ) {

        this.messageValidated = validateMessage(messageValidated);

    }

    // ===================================================== ( constructor end )

    private String validateMessage (String messageRaw) {

        String trimmedMessage = messageRaw == null ? "" : messageRaw.trim();

        String finalMessage = !trimmedMessage.isEmpty() ? trimmedMessage : "Hello World!";

        if (finalMessage.equals("bad-message")) {
            throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
        }

        return finalMessage;

    }

    public String getMessage() {
        return messageValidated;
    }

}
