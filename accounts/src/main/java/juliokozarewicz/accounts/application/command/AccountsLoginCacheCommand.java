package juliokozarewicz.accounts.application.command;

public record AccountsLoginCacheCommand(
    String idUser,
    String pin,
    String refreshToken,
    String reason
) {}