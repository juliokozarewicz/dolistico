package juliokozarewicz.tasks.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import juliokozarewicz.tasks.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.tasks.adapter.rest.dto.ValidationIdentityDTO;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.tasks.application.usecase.TasksDeleteUseCase;
import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class TasksDeleteController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${TASKS_BASE_URL}")
    private String tasksBaseURL;
    // -------------------------------------------------------------------------

    private final TasksDeleteUseCase tasksDeleteUseCase;

    public TasksDeleteController(

        TasksDeleteUseCase tasksDeleteUseCase

    ) {

        this.tasksDeleteUseCase = tasksDeleteUseCase;

    }

    // ===================================================== ( constructor end )

    @DeleteMapping("/{validationIdentityDTO}")
    public ResponseEntity delete(

        // DTO error
        @Valid @PathVariable ValidationIdentityDTO validationIdentityDTO,

        // Request for auth
        HttpServletRequest request

    ) {

        // Data for auth
        Map<String, Object> credentialsData;
        credentialsData = (Map<String, Object>)
        request.getAttribute("credentialsData");

        // Call use case
        tasksDeleteUseCase.execute(
            credentialsData,
            validationIdentityDTO.id()
        );

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.TASKS_DELETED_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.TASKS_DELETED_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.TASKS_DELETED_SUCCESSFULLY.getMessageCode())
            .build()
        );

    }

}