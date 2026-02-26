package juliokozarewicz.helloworld.present.rest.controller;

import jakarta.validation.Valid;
import juliokozarewicz.helloworld.domain.usecase.HelloWorldUseCase;
import juliokozarewicz.helloworld.present.rest.dto.HelloWorldDTO;
import juliokozarewicz.helloworld.present.rest.dto.StandardResponseDTO;
import juliokozarewicz.helloworld.present.rest.exception.GlobalExceptionEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class HelloWorldController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final HelloWorldUseCase helloWorldUseCase;

    public HelloWorldController(
        HelloWorldUseCase helloWorldUseCase
    ) {
        this.helloWorldUseCase = helloWorldUseCase;
    }

    // ===================================================== ( constructor end )

    @GetMapping("/${HELLOWORLD_BASE_URL}")
    public ResponseEntity handle (

        // DTO error
        @Valid HelloWorldDTO helloWorldDTO,
        BindingResult bindingResult

    ) {

        // Call use case
        String result = helloWorldUseCase.execute(helloWorldDTO.message());

        // Standard response
        return ResponseEntity
        .status(200)
        .body(
            new StandardResponseDTO.Builder()
            .statusCode(200)
            .messageCode("HELLO_WORLD_SUCCESS")
                .data(result)
            .build()
        );

    }

}