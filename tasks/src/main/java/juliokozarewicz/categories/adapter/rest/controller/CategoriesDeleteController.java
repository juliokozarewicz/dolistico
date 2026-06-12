package juliokozarewicz.categories.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import juliokozarewicz.categories.adapter.rest.dto.ValidationIdentityDTO;
import juliokozarewicz.categories.application.usecase.CategoriesDeleteUseCase;
import juliokozarewicz.tasks.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("${TASKS_BASE_URL}")
public class CategoriesDeleteController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final CategoriesDeleteUseCase categoriesDeleteUseCase;

    public CategoriesDeleteController(

        CategoriesDeleteUseCase categoriesDeleteUseCase

    ) {

        this.categoriesDeleteUseCase = categoriesDeleteUseCase;

    }

    // ===================================================== ( constructor end )

    @DeleteMapping("/category/{validationIdentityDTO}")
    public ResponseEntity delete(

        // DTO error
        @Valid @PathVariable ValidationIdentityDTO validationIdentityDTO,

        // Request for auth
        HttpServletRequest request

    ) {

        // Data for auth
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID idUser = UUID.fromString(jwt.getSubject());

        // Call use case
        categoriesDeleteUseCase.execute(
            idUser,
            validationIdentityDTO.id()
        );

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.TASKS_CATEGORIES_DELETED_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.TASKS_CATEGORIES_DELETED_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.TASKS_CATEGORIES_DELETED_SUCCESSFULLY.getMessageCode())
            .build()
        );

    }

}