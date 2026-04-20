package juliokozarewicz.categories.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import juliokozarewicz.categories.adapter.rest.dto.CategoriesGetDTO;
import juliokozarewicz.categories.application.usecase.CategoriesGetUseCase;
import juliokozarewicz.tasks.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalSuccessEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("${TASKS_BASE_URL}")
public class CategoriesGetController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${TASKS_BASE_URL}")
    private String tasksBaseURL;
    // -------------------------------------------------------------------------

    private final CategoriesGetUseCase categoriesGetUseCase;

    public CategoriesGetController(

        CategoriesGetUseCase categoriesGetUseCase

    ) {

        this.categoriesGetUseCase = categoriesGetUseCase;

    }

    // ===================================================== ( constructor end )

    @GetMapping("/category")
    public ResponseEntity create (

        // DTO error
        @Valid CategoriesGetDTO categoriesGetDTO,
        BindingResult bindingResult,

        // Request for auth
        HttpServletRequest request

    ) {

        // Data for auth
        Map<String, Object> credentialsData;
        credentialsData = (Map<String, Object>)
        request.getAttribute("credentialsData");

        // Call use case
        Map<String, Object> dataResponse = categoriesGetUseCase.execute(
            credentialsData,
            categoriesGetDTO
        );

        // Content
        List<?> content = (List<?>) dataResponse.get("content");

        // Meta data
        Map<String, Object> metaData = new LinkedHashMap<>();
        metaData.put("currentPage", dataResponse.get("currentPage"));
        metaData.put("totalPages", dataResponse.get("totalPages"));
        metaData.put("totalElementsCurrentPage",  content.size());
        metaData.put("pageSize", dataResponse.get("pageSize"));
        metaData.put("totalElements", dataResponse.get("totalElements"));

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.TASKS_CATEGORIES_RETRIEVED_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.TASKS_CATEGORIES_RETRIEVED_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.TASKS_CATEGORIES_RETRIEVED_SUCCESSFULLY.getMessageCode())
            .data(content)
            .meta(metaData)
            .build()
        );

    }

}