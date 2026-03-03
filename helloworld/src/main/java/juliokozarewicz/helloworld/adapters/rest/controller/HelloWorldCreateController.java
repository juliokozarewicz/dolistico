package juliokozarewicz.helloworld.adapters.rest.controller;

import jakarta.validation.Valid;
import juliokozarewicz.helloworld.adapters.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.helloworld.application.usecase.HelloWorldCreateUseCase;
import juliokozarewicz.helloworld.adapters.rest.dto.HelloWorldCreateDTO;
import juliokozarewicz.helloworld.adapters.rest.dto.StandardResponseDTO;
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
public class HelloWorldCreateController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${HELLOWORLD_BASE_URL}")
    private String helloWorldBaseURL;

    @Value("${DOCUMENTATION_BASE_URL}")
    private String documentationBaseURL;
    // -------------------------------------------------------------------------

    private final HelloWorldCreateUseCase helloWorldCreateUseCase;

    public HelloWorldCreateController(

        HelloWorldCreateUseCase helloWorldCreateUseCase

    ) {

        this.helloWorldCreateUseCase = helloWorldCreateUseCase;

    }

    // ===================================================== ( constructor end )

    @GetMapping("/${HELLOWORLD_BASE_URL}")
    public ResponseEntity handle (

        // DTO error
        @Valid HelloWorldCreateDTO helloWorldCreateDTO,
        BindingResult bindingResult

    ) {

        // Call use case
        String validatedMessage = helloWorldCreateUseCase.execute(helloWorldCreateDTO.message());

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
        .status(GlobalSuccessEnum.HELLO_WORLD_SUCCESS.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.HELLO_WORLD_SUCCESS.getStatusCode())
            .messageCode(GlobalSuccessEnum.HELLO_WORLD_SUCCESS.getMessageCode())
            .data(metaData)
            .links(customLinks)
            .build()
        );

    }

}