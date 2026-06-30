package juliokozarewicz.accounts.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "accounts_config")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class AccountsConfigModel {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "config_name", length = 256, nullable = false, unique = true, updatable = false)
    private String configName;

    @Column(name = "config_value", length = 512, nullable = false)
    private String configValue;

}