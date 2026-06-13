package juliokozarewicz.accounts.adapter.rest.controller;

import jakarta.validation.Valid;
import juliokozarewicz.accounts.adapter.rest.dto.AccountsUpdatePasswordRequestDTO;
import juliokozarewicz.accounts.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.accounts.application.usecase.AccountsUpdatePasswordRequestUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@RestController
@Validated
@RequestMapping("${ACCOUNTS_BASE_URL}")
public class AccountsUpdatePasswordRequestController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsUpdatePasswordRequestUseCase accountsUpdatePasswordRequestUseCase;

    public AccountsUpdatePasswordRequestController(

        AccountsUpdatePasswordRequestUseCase accountsUpdatePasswordRequestUseCase

    ) {

        this.accountsUpdatePasswordRequestUseCase = accountsUpdatePasswordRequestUseCase;

    }

    // ===================================================== ( constructor end )

    @PostMapping("/password/update/request")
    public ResponseEntity<StandardResponseDTO> handle (

        // Locale from Accept-Language
        Locale locale,

        // DTO error
        @Valid @RequestBody AccountsUpdatePasswordRequestDTO accountsUpdatePasswordRequestDTO,
        BindingResult bindingResult

    ) {

        // Call use case
        accountsUpdatePasswordRequestUseCase.execute(
            locale,
            accountsUpdatePasswordRequestDTO
        );

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.ACCOUNTS_UPDATE_PASSWORD_REQUEST_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.ACCOUNTS_UPDATE_PASSWORD_REQUEST_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.ACCOUNTS_UPDATE_PASSWORD_REQUEST_SUCCESSFULLY.getMessageCode())
            .build()
        );

    }

}