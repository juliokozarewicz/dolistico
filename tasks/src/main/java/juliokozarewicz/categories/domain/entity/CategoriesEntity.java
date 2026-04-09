package juliokozarewicz.categories.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class CategoriesEntity {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private UUID idUser;
    private  UUID idCreated;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")

    private  LocalDateTime updatedAt;

    private String categoryName;

    public CategoriesEntity (

        UUID idUser,
        UUID idCreated,
        LocalDateTime createdAt,
        LocalDateTime updatedAtAt,
        String categoryName

    ) {

        this.idUser = idUser;
        this.idCreated = idCreated;
        this.createdAt = createdAt;
        this.updatedAt = updatedAtAt;
        this.categoryName = categoryName;

        validateBusinessRules();

    }

    // ===================================================== ( constructor end )

    private void validateBusinessRules() {

        //------------------------------------------------------- (  user init )

        if (idUser == null) {
            throw new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS);
        }

        //-------------------------------------------------------- (  user end )

        // ---------------------------------------------- ( category name init )

        // Must not be null or blank
        if (categoryName == null || categoryName.isBlank()) {
            throw new DomainException(DomainExceptionEnum.FIELD_CANNOT_BE_EMPTY);
        }

        // Max length: 255 characters
        if (categoryName.length() > 255) {
            throw new DomainException(DomainExceptionEnum.TOO_MANY_CHARACTERS);
        }

        // Forbidden characters validation (security)
        if (!categoryName.matches("^[^<>&'\"/]*$")) {
            throw new DomainException(DomainExceptionEnum.FORBIDDEN_CHARACTERS);
        }

        // ----------------------------------------------- ( category name end )

    }

    // ==================================================== ( behavior )

}