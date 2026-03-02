package juliokozarewicz.tasks.domain.entity;

import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;

public class TasksEntity {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final String createTaskValidated;

    public TasksEntity(

        String createTaskValidated

    ) {

        this.createTaskValidated = validateBusinessRules(createTaskValidated);

    }

    // ===================================================== ( constructor end )

    private String validateBusinessRules (String messageRaw) {

        String trimmedMessage = messageRaw == null ? "" : messageRaw.trim();

        String finalMessage = !trimmedMessage.isEmpty() ? trimmedMessage : "Hello World!";

        if (finalMessage.equalsIgnoreCase("bad-message")) {
            throw new DomainException(DomainExceptionEnum.NO_PERMISSION_TO_ACCESS);
        }

        return finalMessage;

    }

    public String getMessage() {
        return createTaskValidated;
    }

}
