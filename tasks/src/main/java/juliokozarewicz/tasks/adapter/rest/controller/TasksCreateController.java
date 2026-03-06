package juliokozarewicz.tasks.adapter.rest.controller;

import jakarta.validation.Valid;
import juliokozarewicz.tasks.adapter.rest.dto.StandardResponseRestDTO;
import juliokozarewicz.tasks.adapter.rest.dto.TasksCreateRestDTO;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.tasks.application.dto.TasksCreateInputAppDTO;
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
        @Valid @RequestBody TasksCreateRestDTO tasksCreateRestDTO,
        BindingResult bindingResult

    ) {

        // DTO -> Input
        TasksCreateInputAppDTO tasksCreateInputAppDTO = new TasksCreateInputAppDTO(
            tasksCreateRestDTO.taskName(),
            tasksCreateRestDTO.description(),
            tasksCreateRestDTO.category(),
            tasksCreateRestDTO.color(),
            tasksCreateRestDTO.priority(),
            tasksCreateRestDTO.startTime(),
            tasksCreateRestDTO.endTime(),
            tasksCreateRestDTO.location(),
            tasksCreateRestDTO.allDay(),
            tasksCreateRestDTO.reminderTime(),
            tasksCreateRestDTO.notifyActive(),
            tasksCreateRestDTO.status(),
            tasksCreateRestDTO.dueDate()
        );

        // Call use case
        String idCreated = tasksCreateUseCase.execute(tasksCreateInputAppDTO);

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
            new StandardResponseRestDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.CREATE_TASK_SUCCESS.getStatusCode())
            .messageCode(GlobalSuccessEnum.CREATE_TASK_SUCCESS.getMessageCode())
            .data(dataObject)
            .links(customLinks)
            .build()
        );

    }

}