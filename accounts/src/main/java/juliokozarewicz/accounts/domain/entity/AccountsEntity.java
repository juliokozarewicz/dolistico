package juliokozarewicz.accounts.domain.entity;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;

public class AccountsEntity {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final String messageValidated;

    public AccountsEntity(

        String messageValidated

    ) {

        this.messageValidated = validateMessage(messageValidated);

    }

    // ===================================================== ( constructor end )

    private String validateMessage (String messageRaw) {

        String trimmedMessage = messageRaw == null ? "" : messageRaw.trim();

        String finalMessage = !trimmedMessage.isEmpty() ? trimmedMessage : "Hello World!";

        if (finalMessage.equalsIgnoreCase("bad-message")) {
            throw new DomainException(DomainExceptionEnum.NO_PERMISSION_TO_ACCESS);
        }

        return finalMessage;

    }

    public String getMessage() {
        return messageValidated;
    }

}
