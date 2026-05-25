package juliokozarewicz.accounts.application.command;

public record AccountsSendEmailCommand(

    String recipientEmail,
    String subject,
    String messages

) {}