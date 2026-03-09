package juliokozarewicz.tasks.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import juliokozarewicz.tasks.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.tasks.adapter.rest.dto.TasksCreateDTO;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.tasks.application.usecase.TasksCreateUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Validated
public class TasksCreateController {

    // ==================================================== ( constructor init )


    // Env
    // -------------------------------------------------------------------------
    @Value("${TASKS_BASE_URL}")
    private String tasksBaseURL;
    // -------------------------------------------------------------------------

    private final TasksCreateUseCase tasksCreateUseCase;

    public TasksCreateController(

        TasksCreateUseCase tasksCreateUseCase

    ) {

        this.tasksCreateUseCase = tasksCreateUseCase;

    }

    // ===================================================== ( constructor end )

    @PostMapping("/${TASKS_BASE_URL}")
    public ResponseEntity create (

        // DTO error
        @Valid @RequestBody TasksCreateDTO tasksCreateDTO,
        BindingResult bindingResult,

        // Request for auth
        HttpServletRequest request

    ) {

        // Data for auth
        Map<String, Object> credentialsData;
        credentialsData = (Map<String, Object>)
        request.getAttribute("credentialsData");

        // Call use case
        String idCreated = tasksCreateUseCase.execute(
            credentialsData,
            tasksCreateDTO
        );

        // data
        Map<String, String> dataObject = new LinkedHashMap<>();
        dataObject.put("idCreated", idCreated);

        // Links
        Map<String, Object> customLinks = new LinkedHashMap<>();
        customLinks.put("self", new LinkedHashMap<>() {{
            put("href", "/" + tasksBaseURL);
            put("method", "POST");
        }});
        customLinks.put("next", new LinkedHashMap<>() {{
            put("href", "/" + tasksBaseURL);
            put("method", "GET");
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
            .data(dataObject)
            .links(customLinks)
            .build()
        );

    }

}