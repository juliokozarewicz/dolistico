package juliokozarewicz.accounts.adapter.rest.controller;

import jakarta.validation.Valid;
import juliokozarewicz.accounts.adapter.rest.dto.AccountsLogoutUserDTO;
import juliokozarewicz.accounts.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.accounts.application.usecase.AccountsLogoutUserUseCase;
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

@RestController
@Validated
@RequestMapping("${ACCOUNTS_BASE_URL}")
public class AccountsLogoutUserController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsLogoutUserUseCase accountsLogoutUserUseCase;

    public AccountsLogoutUserController(

        AccountsLogoutUserUseCase accountsLogoutUserUseCase

    ) {

        this.accountsLogoutUserUseCase = accountsLogoutUserUseCase;

    }

    // ===================================================== ( constructor end )

    @PostMapping("/logout")
    public ResponseEntity<StandardResponseDTO> handle (

        // DTO error
        @Valid @RequestBody AccountsLogoutUserDTO accountsLogoutUserDTO,
        BindingResult bindingResult

    ) {

        // Call use case
        accountsLogoutUserUseCase.execute(accountsLogoutUserDTO);

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.ACCOUNTS_LOGOUT_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.ACCOUNTS_LOGOUT_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.ACCOUNTS_LOGOUT_SUCCESSFULLY.getMessageCode())
            .build()
        );

    }

}