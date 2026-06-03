package juliokozarewicz.accounts.infrastructure.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import java.time.Instant;

@Entity
@Table(name = "accounts_event_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class AccountsEventLogModel {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(updatable = false, nullable = false)
    private UUID idUser;

    @Column(length = 256, updatable = false, nullable = false)
    private String ipAddress;

    @Column(length = 512, updatable = false, nullable = false)
    private String agent;

    @Column(length = 256, updatable = false, nullable = false)
    private String updateType;

    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @Column(updatable = false, nullable = false)
    private String oldValue;

    @Column(updatable = false, nullable = false)
    private String newValue;

}
