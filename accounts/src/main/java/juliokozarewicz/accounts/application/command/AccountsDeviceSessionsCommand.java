package juliokozarewicz.accounts.application.command;

import java.time.Instant;
import java.util.UUID;

public record AccountsDeviceSessionsCommand(

    UUID id,
    UUID idUser,
    Instant createdAt,
    String ipAddress,
    String location,
    String device,
    String method

) {}