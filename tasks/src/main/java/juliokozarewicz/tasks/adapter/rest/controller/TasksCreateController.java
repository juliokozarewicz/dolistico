package juliokozarewicz.tasks.adapter.rest.controller;

import jakarta.validation.Valid;
import juliokozarewicz.tasks.application.usecase.TasksCreateUseCase;
import juliokozarewicz.tasks.adapter.rest.dto.TasksCreateDTO;
import juliokozarewicz.tasks.adapter.rest.dto.StandardResponseDTO;
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
import java.util.Map;

@RestController
@Validated
public class TasksCreateController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${HELLOWORLD_BASE_URL}")
    private String helloWorldBaseURL;

    @Value("${DOCUMENTATION_BASE_URL}")
    private String documentationBaseURL;
    // -------------------------------------------------------------------------

    private final TasksCreateUseCase tasksCreateUseCase;

    public TasksCreateController(

        TasksCreateUseCase tasksCreateUseCase

    ) {

        this.tasksCreateUseCase = tasksCreateUseCase;

    }

    // ===================================================== ( constructor end )

    @GetMapping("/${HELLOWORLD_BASE_URL}")
    public ResponseEntity handle (

        // DTO error
        @Valid TasksCreateDTO tasksCreateDTO,
        BindingResult bindingResult

    ) {

        // Call use case
        String validatedMessage = tasksCreateUseCase.execute(tasksCreateDTO.message());

        // Message
        Map<String, Object> metaData = new LinkedHashMap<>();
        metaData.put("message", validatedMessage);

        // Links
        Map<String, String> customLinks = new LinkedHashMap<>();
        customLinks.put("self", "/" + helloWorldBaseURL);
        customLinks.put("documentation", "/" + documentationBaseURL);
        customLinks.put("swaggerDocumentation", "/" + documentationBaseURL + "/swagger");

        // Standard response
        return ResponseEntity
        .status(200)
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(200)
            .messageCode("HELLO_WORLD_SUCCESS")
            .data(metaData)
            .links(customLinks)
            .build()
        );

    }

}