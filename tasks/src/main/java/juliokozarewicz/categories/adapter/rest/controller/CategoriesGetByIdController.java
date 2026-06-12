package juliokozarewicz.categories.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import juliokozarewicz.categories.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.categories.adapter.rest.dto.ValidationIdentityDTO;
import juliokozarewicz.categories.application.command.CategoriesGetResponseCommand;
import juliokozarewicz.categories.application.usecase.CategoriesGetByIdUseCase;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
public class CategoriesGetByIdController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final CategoriesGetByIdUseCase categoriesGetByIdUseCase;

    public CategoriesGetByIdController(

        CategoriesGetByIdUseCase categoriesGetByIdUseCase

    ) {

        this.categoriesGetByIdUseCase = categoriesGetByIdUseCase;

    }

    // ===================================================== ( constructor end )

    @GetMapping("/category/{validationIdentityDTO}")
    public ResponseEntity create (

        // DTO error
        @Valid @PathVariable ValidationIdentityDTO validationIdentityDTO,

        // Request for auth
        HttpServletRequest request

    ) {

        // Data for auth
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID idUser = UUID.fromString(jwt.getSubject());

        // Call use case
         CategoriesGetResponseCommand dataResponse = categoriesGetByIdUseCase.execute(
            idUser,
            validationIdentityDTO.id()
        );

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.TASKS_CATEGORIES_RETRIEVED_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.TASKS_CATEGORIES_RETRIEVED_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.TASKS_CATEGORIES_RETRIEVED_SUCCESSFULLY.getMessageCode())
            .data(dataResponse)
            .build()
        );

    }

}