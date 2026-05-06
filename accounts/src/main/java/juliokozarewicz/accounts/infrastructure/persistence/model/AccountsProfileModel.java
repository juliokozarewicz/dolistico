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
public class AccountsProfileModel {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID idUser;

    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(length = 512)
    private String profileImage;

    @Column(length = 256)
    private String fullName;

    @Column(length = 25)
    private String phone;

    @Column(length = 256)
    private String identityDocument;

    @Column(length = 256)
    private String gender;

    @Column(length = 50)
    private String birthdate;

    @Column(length = 256)
    private String biography;

    @Column(length = 50)
    private String language;

    @Column(length = 100)
    private String theme;

}
