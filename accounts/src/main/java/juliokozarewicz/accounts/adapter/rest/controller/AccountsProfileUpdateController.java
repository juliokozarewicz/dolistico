package juliokozarewicz.accounts.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import juliokozarewicz.accounts.adapter.rest.dto.AccountsProfileUpdateDTO;
import juliokozarewicz.accounts.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.accounts.application.usecase.AccountsProfileUpdateUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("${ACCOUNTS_BASE_URL}")
public class AccountsProfileUpdateController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsProfileUpdateUseCase accountsProfileUpdateUseCase;

    public AccountsProfileUpdateController(

        AccountsProfileUpdateUseCase accountsProfileUpdateUseCase

    ) {

        this.accountsProfileUpdateUseCase = accountsProfileUpdateUseCase;

    }

    // ===================================================== ( constructor end )

    @PutMapping("/profile")
    public ResponseEntity<StandardResponseDTO> handle (

        // DTO error
        @Valid @RequestBody AccountsProfileUpdateDTO accountsProfileUpdateDTO,
        BindingResult bindingResult,

        // Request for auth
        HttpServletRequest request

    ) {

        // Data for auth
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();

        UUID idUser = UUID.fromString(jwt.getSubject());

        // Call use case
        accountsProfileUpdateUseCase.execute(idUser, accountsProfileUpdateDTO);

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.ACCOUNTS_PROFILE_UPDATED_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.ACCOUNTS_PROFILE_UPDATED_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.ACCOUNTS_PROFILE_UPDATED_SUCCESSFULLY.getMessageCode())
            .build()
        );

    }

}