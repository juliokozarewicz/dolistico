package juliokozarewicz.accounts.adapter.rest.controller;

import jakarta.validation.Valid;
import juliokozarewicz.accounts.adapter.rest.dto.AccountsCreateDTO;
import juliokozarewicz.accounts.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.accounts.application.usecase.AccountsCreateUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Validated
@RequestMapping("${ACCOUNTS_BASE_URL}")
public class AccountsCreateController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${ACCOUNTS_BASE_URL}")
    private String accountsBaseURL;
    // -------------------------------------------------------------------------

    private final AccountsCreateUseCase accountsCreateUseCase;

    public AccountsCreateController(

        AccountsCreateUseCase accountsCreateUseCase

    ) {

        this.accountsCreateUseCase = accountsCreateUseCase;

    }

    // ===================================================== ( constructor end )

    @PostMapping()
    public ResponseEntity handle (

        // DTO error
        @Valid AccountsCreateDTO accountsCreateDTO,
        BindingResult bindingResult

    ) {

        // Call use case
        String validatedMessage = accountsCreateUseCase.execute(accountsCreateDTO.message());

        // Message
        Map<String, Object> metaData = new LinkedHashMap<>();
        metaData.put("message", validatedMessage);

        // Links
        Map<String, Object> customLinks = new LinkedHashMap<>();
        customLinks.put("self", new LinkedHashMap<>() {{
            put("href", "/" + accountsBaseURL);
            put("method", "GET");
        }});

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.ACCOUNTS_CREATED_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.ACCOUNTS_CREATED_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.ACCOUNTS_CREATED_SUCCESSFULLY.getMessageCode())
            .data(metaData)
            .links(customLinks)
            .build()
        );

    }

}