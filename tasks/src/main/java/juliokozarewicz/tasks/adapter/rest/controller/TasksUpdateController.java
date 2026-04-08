package juliokozarewicz.tasks.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import juliokozarewicz.tasks.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.tasks.adapter.rest.dto.TasksCreateUpadateDTO;
import juliokozarewicz.tasks.adapter.rest.dto.ValidationIdentityDTO;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.tasks.application.usecase.TasksUpdateUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@RestController
@Validated
public class TasksUpdateController {

    // ==================================================== ( constructor init )


    // Env
    // -------------------------------------------------------------------------
    @Value("${TASKS_BASE_URL}")
    private String tasksBaseURL;
    // -------------------------------------------------------------------------

    private final TasksUpdateUseCase tasksUpdateUseCase;

    public TasksUpdateController(

        TasksUpdateUseCase tasksUpdateUseCase

    ) {

        this.tasksUpdateUseCase = tasksUpdateUseCase;

    }

    // ===================================================== ( constructor end )

    @PutMapping("/${TASKS_BASE_URL}/{validationIdentityDTO}")
    public ResponseEntity create (

        // DTO error
        @Valid @PathVariable ValidationIdentityDTO validationIdentityDTO,
        @Valid @RequestBody TasksCreateUpadateDTO tasksCreateUpadateDTO,
        BindingResult bindingResult,

        // Request for auth
        HttpServletRequest request

    ) {

        // Data for auth
        Map<String, Object> credentialsData;
        credentialsData = (Map<String, Object>)
        request.getAttribute("credentialsData");

        // Call use case
        tasksUpdateUseCase.execute(
            credentialsData,
            UUID.fromString(validationIdentityDTO.id()),
            tasksCreateUpadateDTO
        );

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.TASK_UPDATED_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.TASK_UPDATED_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.TASK_UPDATED_SUCCESSFULLY.getMessageCode())
            .build()
        );

    }

}