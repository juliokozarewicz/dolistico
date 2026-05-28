package juliokozarewicz.accounts.application.command;

public record AccountsLoginCacheCommand(
    String idUser,
    String reason
) {}