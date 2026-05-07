package juliokozarewicz.accounts.adapter.rest.controller;

import jakarta.validation.Valid;
import juliokozarewicz.accounts.adapter.rest.dto.AccountsUpdatePasswordLinkDTO;
import juliokozarewicz.accounts.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.accounts.application.usecase.AccountsUpdatePasswordLinkUseCase;
import org.springframework.beans.factory.annotation.Value;
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
public class AccountsUpdatePasswordLinkController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsUpdatePasswordLinkUseCase accountsUpdatePasswordLinkUseCase;

    public AccountsUpdatePasswordLinkController(

        AccountsUpdatePasswordLinkUseCase accountsUpdatePasswordLinkUseCase

    ) {

        this.accountsUpdatePasswordLinkUseCase = accountsUpdatePasswordLinkUseCase;

    }

    // ===================================================== ( constructor end )

    @PostMapping("/update-password")
    public ResponseEntity<StandardResponseDTO> handle (

        // DTO error
        @Valid @RequestBody AccountsUpdatePasswordLinkDTO accountsUpdatePasswordLinkDTO,
        BindingResult bindingResult

    ) {

        // Call use case (no DB changes yet)
        accountsUpdatePasswordLinkUseCase.execute(accountsUpdatePasswordLinkDTO);

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.ACCOUNTS_PASSWORD_RESET_LINK_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.ACCOUNTS_PASSWORD_RESET_LINK_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.ACCOUNTS_PASSWORD_RESET_LINK_SUCCESSFULLY.getMessageCode())
            .build()
        );

    }

}