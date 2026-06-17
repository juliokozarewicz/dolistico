package juliokozarewicz.accounts.domain.entity;

import java.time.Instant;
import java.util.UUID;

public class AccountsDeviceSessionEntity {

    private final UUID id;
    private final UUID idUser;
    private final Instant createdAt;
    private final String ipAddress;
    private final String location;
    private final String device;
    private final String method;

    public AccountsDeviceSessionEntity(
        UUID id,
        UUID idUser,
        Instant createdAt,
        String ipAddress,
        String location,
        String device,
        String method
    ) {
        this.id = id;
        this.idUser = idUser;
        this.createdAt = createdAt;
        this.ipAddress = ipAddress;
        this.location = location;
        this.device = device;
        this.method = method;
    }

    public UUID getId() {
        return id;
    }

    public UUID getIdUser() {
        return idUser;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getLocation() {
        return location;
    }

    public String getDevice() {
        return device;
    }

    public String getMethod() {
        return method;
    }
}