package juliokozarewicz.tasks.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import juliokozarewicz.tasks.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.tasks.adapter.rest.dto.TasksGetDTO;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.tasks.application.usecase.TasksGetUseCase;
import juliokozarewicz.tasks.domain.entity.TasksEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
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

    @GetMapping("/${TASKS_BASE_URL}")
    public ResponseEntity create (

        // DTO error
        @Valid TasksGetDTO tasksGetDTO,
        BindingResult bindingResult,

        // Request for auth
        HttpServletRequest request

    ) {

        // Data for auth
        Map<String, Object> credentialsData;
        credentialsData = (Map<String, Object>)
        request.getAttribute("credentialsData");

        // Call use case
        List<TasksEntity> dataReturned = tasksGetUseCase.execute(
            credentialsData,
            tasksGetDTO
        );

        // Meta data
        Map<String, Object> metaData = new LinkedHashMap<>();
        metaData.put("totalItems", dataReturned.size());

        // Links
        Map<String, Object> customLinks = new LinkedHashMap<>();
        customLinks.put("self", new LinkedHashMap<>() {{
            put("href", "/" + tasksBaseURL);
            put("method", "GET");
        }});
        customLinks.put("next", new LinkedHashMap<>() {{
            put("href", "/" + tasksBaseURL);
            put("method", "PUT");
        }});

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.CREATE_TASK_SUCCESS.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.CREATE_TASK_SUCCESS.getStatusCode())
            .messageCode(GlobalSuccessEnum.CREATE_TASK_SUCCESS.getMessageCode())
            .data(dataReturned)
            .meta(metaData)
            .links(customLinks)
            .build()
        );

    }

}