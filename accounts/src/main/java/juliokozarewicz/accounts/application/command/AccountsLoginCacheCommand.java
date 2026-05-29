package juliokozarewicz.accounts.application.command;

public record AccountsLoginCacheCommand(
    String pin,
    String refreshToken
) {}