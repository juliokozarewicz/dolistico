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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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
public class
TasksUpdateController {

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

    @PutMapping("/{validationIdentityDTO}")
    public ResponseEntity create (

        // DTO error
        @Valid @PathVariable ValidationIdentityDTO validationIdentityDTO,
        @Valid @RequestBody TasksCreateUpadateDTO tasksCreateUpadateDTO,
        BindingResult bindingResult

    ) {

        // Data for auth
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID idUser = UUID.fromString(jwt.getSubject());

        // Call use case
        tasksUpdateUseCase.execute(
            idUser,
            UUID.fromString(validationIdentityDTO.id()),
            tasksCreateUpadateDTO
        );

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.TASKS_UPDATED_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.TASKS_UPDATED_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.TASKS_UPDATED_SUCCESSFULLY.getMessageCode())
            .build()
        );

    }

}