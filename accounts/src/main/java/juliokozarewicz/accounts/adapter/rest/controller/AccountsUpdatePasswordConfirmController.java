package juliokozarewicz.accounts.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import juliokozarewicz.accounts.adapter.rest.dto.AccountsRequestDTO;
import juliokozarewicz.accounts.adapter.rest.dto.AccountsUpdatePasswordConfirmConfirmDTO;
import juliokozarewicz.accounts.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.accounts.application.usecase.AccountsUpdatePasswordConfirmUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("${ACCOUNTS_BASE_URL}")
public class AccountsUpdatePasswordConfirmController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsUpdatePasswordConfirmUseCase accountsUpdatePasswordConfirmUseCase;
    private final AccountsRequestDTO accountsRequestDTO;

    public AccountsUpdatePasswordConfirmController(

        AccountsUpdatePasswordConfirmUseCase accountsUpdatePasswordConfirmUseCase,
        AccountsRequestDTO accountsRequestDTO

    ) {

        this.accountsUpdatePasswordConfirmUseCase = accountsUpdatePasswordConfirmUseCase;
        this.accountsRequestDTO = accountsRequestDTO;

    }

    // ===================================================== ( constructor end )

    @PatchMapping("/password/update/confirm")
    public ResponseEntity<StandardResponseDTO> handle (

        // Locale from Accept-Language
        Locale locale,

        // DTO error
        @Valid @RequestBody AccountsUpdatePasswordConfirmConfirmDTO accountsUpdatePasswordConfirmDTO,
        BindingResult bindingResult,

        HttpServletRequest request

    ) {

        // Request data
        // ---------------------------------------------------------------------
        String userIp = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
            .filter(ip -> !ip.isBlank())
            .map(ip -> ip.contains(",") ? ip.split(",")[0].trim() : ip)
            .orElse(request.getRemoteAddr());

        String userAgent = request.getHeader("User-Agent");

        //validation request data
        accountsRequestDTO.validateUserIp(userIp);
        accountsRequestDTO.validateUserAgent(userAgent);
        // ---------------------------------------------------------------------

        // Call use case
        accountsUpdatePasswordConfirmUseCase.execute(
            userIp,
            userAgent,
            locale,
            accountsUpdatePasswordConfirmDTO
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