package juliokozarewicz.accounts.adapter.rest.controller;

import jakarta.validation.Valid;
import juliokozarewicz.accounts.adapter.rest.dto.AccountsLoginRequestDTO;
import juliokozarewicz.accounts.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.accounts.application.usecase.AccountsLoginRequestUseCase;
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
import java.util.Map;

@RestController
@Validated
@RequestMapping("${ACCOUNTS_BASE_URL}")
public class AccountsLoginRequestController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsLoginRequestUseCase accountsLoginRequestUseCase;

    public AccountsLoginRequestController(

        AccountsLoginRequestUseCase accountsLoginRequestUseCase

    ) {

        this.accountsLoginRequestUseCase = accountsLoginRequestUseCase;

    }

    // ===================================================== ( constructor end )

    @PostMapping("/login/request")
    public ResponseEntity<StandardResponseDTO> handle (

        // Locale from Accept-Language
        Locale locale,

        // DTO error
        @Valid @RequestBody AccountsLoginRequestDTO accountsLoginRequestDTO,
        BindingResult bindingResult

    ) {

        // Call use case
        String userLoginToken = accountsLoginRequestUseCase.execute(
            locale,
            accountsLoginRequestDTO
        );

        // User login token response
        Map<String, Object> userLoginTokenResponse = new java.util.LinkedHashMap<>();
        userLoginTokenResponse.put("userLoginToken", userLoginToken);

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.ACCOUNTS_LOGIN_REQUEST_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.ACCOUNTS_LOGIN_REQUEST_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.ACCOUNTS_LOGIN_REQUEST_SUCCESSFULLY.getMessageCode())
            .data(userLoginTokenResponse)
            .build()
        );

    }

}