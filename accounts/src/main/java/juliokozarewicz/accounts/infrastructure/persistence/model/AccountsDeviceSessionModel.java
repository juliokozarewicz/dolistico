package juliokozarewicz.accounts.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts_devices")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class AccountsDeviceSessionModel {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(updatable = false, nullable = false)
    private UUID idUser;

    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @Column(length = 256, updatable = false, nullable = false)
    private String ipAddress;

    @Column(length = 256, updatable = false, nullable = false)
    private String location;

    @Column(length = 256, updatable = false, nullable = false)
    private String device;

    @Column(length = 256, updatable = false, nullable = false)
    private String method;

}
