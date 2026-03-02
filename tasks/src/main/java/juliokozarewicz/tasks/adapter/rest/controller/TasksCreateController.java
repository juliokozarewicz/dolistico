package juliokozarewicz.tasks.adapter.rest.controller;

import jakarta.validation.Valid;
import juliokozarewicz.tasks.application.usecase.TasksCreateUseCase;
import juliokozarewicz.tasks.adapter.rest.dto.TasksCreateDTO;
import juliokozarewicz.tasks.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.tasks.application.input.TasksCreateInput;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
        BindingResult bindingResult

    ) {

        // DTO -> Input
        TasksCreateInput tasksCreateInput = new TasksCreateInput(
            tasksCreateDTO.taskName(),
            tasksCreateDTO.description(),
            tasksCreateDTO.category(),
            tasksCreateDTO.color(),
            tasksCreateDTO.priority(),
            tasksCreateDTO.startTime(),
            tasksCreateDTO.endTime(),
            tasksCreateDTO.location(),
            tasksCreateDTO.allDay(),
            tasksCreateDTO.reminderTime(),
            tasksCreateDTO.notifyActive(),
            tasksCreateDTO.status(),
            tasksCreateDTO.dueDate()
        );

        // Call use case
        tasksCreateUseCase.execute(tasksCreateInput);

        // Links
        Map<String, String> customLinks = new LinkedHashMap<>();
        customLinks.put("self", "/" + tasksBaseURL);

        // Standard response
        return ResponseEntity
        .status(201)
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(201)
            .messageCode("CREATE_TASK_SUCCESS")
            .links(customLinks)
            .build()
        );

    }

}