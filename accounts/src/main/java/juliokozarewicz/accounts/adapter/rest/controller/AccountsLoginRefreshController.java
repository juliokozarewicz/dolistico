package juliokozarewicz.accounts.adapter.rest.controller;

import jakarta.validation.Valid;
import juliokozarewicz.accounts.adapter.rest.dto.AccountsLoginRefreshDTO;
import juliokozarewicz.accounts.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.accounts.application.usecase.AccountsLoginRefreshUseCase;
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
import java.util.Map;

@RestController
@Validated
@RequestMapping("${ACCOUNTS_BASE_URL}")
public class AccountsLoginRefreshController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsLoginRefreshUseCase accountsLoginRefreshUseCase;

    public AccountsLoginRefreshController(

        AccountsLoginRefreshUseCase accountsLoginRefreshUseCase

    ) {

        this.accountsLoginRefreshUseCase = accountsLoginRefreshUseCase;

    }

    // ===================================================== ( constructor end )

    @PostMapping("/login/refresh")
    public ResponseEntity<StandardResponseDTO> handle (

        // DTO error
        @Valid @RequestBody AccountsLoginRefreshDTO accountsLoginRefreshDTO,
        BindingResult bindingResult

    ) {

        // Call use case
        Map<String, Object> credentials = accountsLoginRefreshUseCase.execute(
            accountsLoginRefreshDTO
        );

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.ACCOUNTS_LOGIN_REFRESH_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.ACCOUNTS_LOGIN_REFRESH_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.ACCOUNTS_LOGIN_REFRESH_SUCCESSFULLY.getMessageCode())
            .data(credentials)
            .build()
        );

    }

}