package juliokozarewicz.accounts.application.command;

public record AccountsDeleteCacheCommand(
    String idUser,
    String reason
) {}