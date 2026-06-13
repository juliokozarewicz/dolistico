package juliokozarewicz.accounts.application.command;

public record AccountsUpdateEmailCacheCommand(
    String idUser,
    String pin,
    String newEmail,
    String reason
) {}