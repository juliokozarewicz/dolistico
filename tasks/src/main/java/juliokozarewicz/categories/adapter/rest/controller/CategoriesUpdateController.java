package juliokozarewicz.categories.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import juliokozarewicz.categories.adapter.rest.dto.CategoriesCreateUpadateDTO;
import juliokozarewicz.categories.application.usecase.CategoriesUpdateUseCase;
import juliokozarewicz.tasks.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.tasks.adapter.rest.dto.ValidationIdentityDTO;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalSuccessEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("${TASKS_BASE_URL}")
public class CategoriesUpdateController {

    // ==================================================== ( constructor init )


    // Env
    // -------------------------------------------------------------------------
    @Value("${TASKS_BASE_URL}")
    private String tasksBaseURL;
    // -------------------------------------------------------------------------

    private final CategoriesUpdateUseCase categoriesUpdateUseCase;

    public CategoriesUpdateController(

        CategoriesUpdateUseCase categoriesUpdateUseCase

    ) {

        this.categoriesUpdateUseCase = categoriesUpdateUseCase;

    }

    // ===================================================== ( constructor end )

    @PutMapping("/category/{validationIdentityDTO}")
    public ResponseEntity create (

        // DTO error
        @Valid @PathVariable ValidationIdentityDTO validationIdentityDTO,
        @Valid @RequestBody CategoriesCreateUpadateDTO categoriesCreateUpadateDTO,
        BindingResult bindingResult,

        // Request for auth
        HttpServletRequest request

    ) {

        // Data for auth
        Map<String, Object> credentialsData;
        credentialsData = (Map<String, Object>)
        request.getAttribute("credentialsData");

        // Call use case
        categoriesUpdateUseCase.execute(
            credentialsData,
            UUID.fromString(validationIdentityDTO.id()),
            categoriesCreateUpadateDTO
        );

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.TASKS_CATEGORIES_UPDATED_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.TASKS_CATEGORIES_UPDATED_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.TASKS_CATEGORIES_UPDATED_SUCCESSFULLY.getMessageCode())
            .build()
        );

    }

}