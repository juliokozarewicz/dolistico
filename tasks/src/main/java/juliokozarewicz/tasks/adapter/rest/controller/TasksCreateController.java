package juliokozarewicz.tasks.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import juliokozarewicz.tasks.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.tasks.adapter.rest.dto.TasksCreateUpadateDTO;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.tasks.application.usecase.TasksCreateUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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
import java.util.UUID;

@RestController
@Validated
@RequestMapping("${TASKS_BASE_URL}")
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

    @PostMapping()
    public ResponseEntity create (

        // DTO error
        @Valid @RequestBody TasksCreateUpadateDTO tasksCreateUpadateDTO,
        BindingResult bindingResult

    ) {

        // Data for auth
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID idUser = UUID.fromString(jwt.getSubject());

        // Call use case
        String idCreated = tasksCreateUseCase.execute(
            idUser,
            tasksCreateUpadateDTO
        );

        // data
        Map<String, String> dataObject = new LinkedHashMap<>();
        dataObject.put("idCreated", idCreated);

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.TASK_CREATED_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.TASK_CREATED_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.TASK_CREATED_SUCCESSFULLY.getMessageCode())
            .data(dataObject)
            .build()
        );

    }

}