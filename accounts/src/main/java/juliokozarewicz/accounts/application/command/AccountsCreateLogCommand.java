package juliokozarewicz.accounts.application.command;

import java.time.Instant;

public record AccountsCreateLogCommand (
    String idUser,
    String ipAddress,
    String agent,
    String updateType,
    Instant timestamp,
    String oldValue,
    String newValue
) {}
