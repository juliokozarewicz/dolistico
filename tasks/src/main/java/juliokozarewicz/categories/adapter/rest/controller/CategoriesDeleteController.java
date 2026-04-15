package juliokozarewicz.categories.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import juliokozarewicz.categories.adapter.rest.dto.ValidationIdentityDTO;
import juliokozarewicz.tasks.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.categories.application.usecase.CategoriesDeleteUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RestController
@Validated
public class CategoriesDeleteController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${TASKS_BASE_URL}")
    private String tasksBaseURL;
    // -------------------------------------------------------------------------

    private final CategoriesDeleteUseCase categoriesDeleteUseCase;

    public CategoriesDeleteController(

        CategoriesDeleteUseCase categoriesDeleteUseCase

    ) {

        this.categoriesDeleteUseCase = categoriesDeleteUseCase;

    }

    // ===================================================== ( constructor end )

    @DeleteMapping("/${TASKS_BASE_URL}/category/{validationIdentityDTO}")
    public ResponseEntity delete(

        // DTO error
        @Valid @PathVariable ValidationIdentityDTO validationIdentityDTO,

        // Request for auth
        HttpServletRequest request

    ) {

        // Data for auth
        Map<String, Object> credentialsData;
        credentialsData = (Map<String, Object>)
        request.getAttribute("credentialsData");

        // Call use case
        categoriesDeleteUseCase.execute(
            credentialsData,
            validationIdentityDTO.id()
        );

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.CATEGORY_DELETED_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.CATEGORY_DELETED_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.CATEGORY_DELETED_SUCCESSFULLY.getMessageCode())
            .build()
        );

    }

}