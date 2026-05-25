package juliokozarewicz.accounts.application.command;

public record AccountsUpdatePasswordCacheCommand(
    String idUser,
    String reason
) {}