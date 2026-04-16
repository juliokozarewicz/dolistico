package juliokozarewicz.helloworld.domain.entity;

import juliokozarewicz.helloworld.domain.exception.DomainException;
import juliokozarewicz.helloworld.domain.exception.DomainExceptionEnum;

public class HelloWorldEntity {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final String messageValidated;

    public HelloWorldEntity(

        String messageValidated

    ) {

        this.messageValidated = validateMessage(messageValidated);

    }

    // ===================================================== ( constructor end )

    private String validateMessage (String messageRaw) {

        String trimmedMessage = messageRaw == null ? "" : messageRaw.trim();

        String finalMessage = !trimmedMessage.isEmpty() ? trimmedMessage : "Hello World!";

        if (finalMessage.equalsIgnoreCase("bad-message")) {
            throw new DomainException(DomainExceptionEnum.TASKS_NO_PERMISSION_TO_ACCESS);
        }

        return finalMessage;

    }

    public String getMessage() {
        return messageValidated;
    }

}
