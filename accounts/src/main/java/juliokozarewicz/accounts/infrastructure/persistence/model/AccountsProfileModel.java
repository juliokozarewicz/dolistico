package juliokozarewicz.accounts.infrastructure.persistence.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts_profile", schema = "accounts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
class AccountsProfileModel {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(updatable = false, nullable = false)
    private UUID idUser;

    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(length = 555, nullable = true)
    private String profileImage;

    @Column(length = 256, nullable = true)
    private String fullName;

    @Column(length = 25, nullable = true)
    private String phone;

    @Column(length = 256, nullable = true)
    private String identityDocument;

    @Column(length = 256, nullable = true)
    private String gender;

    @Column(length = 50, nullable = true)
    private String birthdate;

    @Column(length = 256, nullable = true)
    private String biography;

    @Column(length = 50, nullable = true)
    private String language;

    @Column(length = 100, nullable = true)
    private String theme;

}
