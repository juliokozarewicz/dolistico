package juliokozarewicz.tasks.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import juliokozarewicz.tasks.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.tasks.adapter.rest.dto.TasksGetDTO;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.tasks.application.usecase.TasksGetUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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
import java.util.UUID;

@RestController
@Validated
@RequestMapping("${TASKS_BASE_URL}")
public class TasksGetController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${TASKS_BASE_URL}")
    private String tasksBaseURL;
    // -------------------------------------------------------------------------

    private final TasksGetUseCase tasksGetUseCase;

    public TasksGetController (

        TasksGetUseCase tasksGetUseCase

    ) {

        this.tasksGetUseCase = tasksGetUseCase;

    }

    // ===================================================== ( constructor end )

    @GetMapping()
    public ResponseEntity create (

        // DTO error
        @Valid TasksGetDTO tasksGetDTO,
        BindingResult bindingResult,

        // Request for auth
        HttpServletRequest request

    ) {

        // Data for auth
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();

        UUID idUser = UUID.fromString(jwt.getSubject());

        // Call use case
        Map<String, Object> dataResponse = tasksGetUseCase.execute(
            idUser,
            tasksGetDTO
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
        .status(GlobalSuccessEnum.TASKS_RETRIEVED_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.TASKS_RETRIEVED_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.TASKS_RETRIEVED_SUCCESSFULLY.getMessageCode())
            .data(content)
            .meta(metaData)
            .build()
        );

    }

}