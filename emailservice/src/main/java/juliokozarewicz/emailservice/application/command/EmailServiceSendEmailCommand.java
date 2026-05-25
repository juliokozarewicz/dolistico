package juliokozarewicz.emailservice.application.command;

public record EmailServiceSendEmailCommand(

    String recipientEmail,
    String subject,
    String messages

) {}