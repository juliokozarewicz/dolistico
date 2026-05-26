package juliokozarewicz.accounts.adapter.rest.controller;

import jakarta.validation.Valid;
import juliokozarewicz.accounts.adapter.rest.dto.AccountsUpdatePasswordDTO;
import juliokozarewicz.accounts.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.accounts.application.usecase.AccountsUpdatePasswordUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@RestController
@Validated
@RequestMapping("${ACCOUNTS_BASE_URL}")
public class AccountsUpdatePasswordController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsUpdatePasswordUseCase accountsUpdatePasswordUseCase;

    public AccountsUpdatePasswordController(

        AccountsUpdatePasswordUseCase accountsUpdatePasswordUseCase

    ) {

        this.accountsUpdatePasswordUseCase = accountsUpdatePasswordUseCase;

    }

    // ===================================================== ( constructor end )

    @PatchMapping("/update-password")
    public ResponseEntity<StandardResponseDTO> handle (

        // Locale from Accept-Language
        Locale locale,

        // DTO error
        @Valid @RequestBody AccountsUpdatePasswordDTO accountsUpdatePasswordDTO,
        BindingResult bindingResult

    ) {

        // Call use case
        accountsUpdatePasswordUseCase.execute(
            locale,
            accountsUpdatePasswordDTO
        );

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.ACCOUNTS_UPDATE_PASSWORD_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.ACCOUNTS_UPDATE_PASSWORD_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.ACCOUNTS_UPDATE_PASSWORD_SUCCESSFULLY.getMessageCode())
            .build()
        );

    }

}