package juliokozarewicz.accounts.application.command;

import java.time.Instant;
import java.util.UUID;

public record AccountsDeviceSessionsResponseCommand(

    UUID id,
    Instant createdAt,
    String ipAddress,
    String location,
    String device,
    String method

) {}