package juliokozarewicz.accounts.adapter.rest.controller;

import juliokozarewicz.accounts.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.accounts.application.usecase.AccountsDeleteRequestUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("${ACCOUNTS_BASE_URL}")
public class AccountsDeleteRequestController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsDeleteRequestUseCase accountsDeleteRequestUseCase;

    public AccountsDeleteRequestController(

        AccountsDeleteRequestUseCase accountsDeleteRequestUseCase

    ) {

        this.accountsDeleteRequestUseCase = accountsDeleteRequestUseCase;

    }

    // ===================================================== ( constructor end )

    @PostMapping("/delete/request")
    public ResponseEntity<StandardResponseDTO> handle (

        // Locale from Accept-Language
        Locale locale

    ) {

        // Data for auth
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID idUser = UUID.fromString(jwt.getSubject());

        // Call use case
        accountsDeleteRequestUseCase.execute(locale, idUser);

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.ACCOUNTS_DELETE_REQUEST_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.ACCOUNTS_DELETE_REQUEST_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.ACCOUNTS_DELETE_REQUEST_SUCCESSFULLY.getMessageCode())
            .build()
        );

    }

}