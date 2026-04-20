package juliokozarewicz.categories.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import juliokozarewicz.categories.adapter.rest.dto.CategoriesCreateUpadateDTO;
import juliokozarewicz.categories.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.categories.application.usecase.CategoriesCreateUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Validated
@RequestMapping("${TASKS_BASE_URL}")
public class CategoriesCreateController {

    // ==================================================== ( constructor init )


    // Env
    // -------------------------------------------------------------------------
    @Value("${TASKS_BASE_URL}")
    private String tasksBaseURL;
    // -------------------------------------------------------------------------

    private final CategoriesCreateUseCase categoriesCreateUseCase;

    public CategoriesCreateController(

        CategoriesCreateUseCase categoriesCreateUseCase

    ) {

        this.categoriesCreateUseCase = categoriesCreateUseCase;

    }

    // ===================================================== ( constructor end )

    @PostMapping("/category")
    public ResponseEntity create (

        // DTO error
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
        String idCreated = categoriesCreateUseCase.execute(
            credentialsData,
            categoriesCreateUpadateDTO
        );

        // data
        Map<String, String> dataObject = new LinkedHashMap<>();
        dataObject.put("idCreated", idCreated);

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.TASKS_CATEGORIES_CREATED_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.TASKS_CATEGORIES_CREATED_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.TASKS_CATEGORIES_CREATED_SUCCESSFULLY.getMessageCode())
            .data(dataObject)
            .build()
        );

    }

}