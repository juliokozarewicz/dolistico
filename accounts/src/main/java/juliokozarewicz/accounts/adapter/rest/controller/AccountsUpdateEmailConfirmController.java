package juliokozarewicz.accounts.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import juliokozarewicz.accounts.adapter.rest.dto.AccountsRequestDTO;
import juliokozarewicz.accounts.adapter.rest.dto.AccountsUpdateEmailConfirmDTO;
import juliokozarewicz.accounts.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.accounts.application.usecase.AccountsUpdateEmailConfirmUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("${ACCOUNTS_BASE_URL}")
public class AccountsUpdateEmailConfirmController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsUpdateEmailConfirmUseCase accountsUpdateEmailConfirmUseCase;
    private final AccountsRequestDTO accountsRequestDTO;

    public AccountsUpdateEmailConfirmController(

        AccountsUpdateEmailConfirmUseCase accountsUpdateEmailConfirmUseCase,
        AccountsRequestDTO accountsRequestDTO

    ) {

        this.accountsUpdateEmailConfirmUseCase = accountsUpdateEmailConfirmUseCase;
        this.accountsRequestDTO = accountsRequestDTO;

    }

    // ===================================================== ( constructor end )

    @PatchMapping("/email/update/confirm")
    public ResponseEntity<StandardResponseDTO> handle (

        // Locale from Accept-Language
        Locale locale,

        // DTO error
        @Valid @RequestBody AccountsUpdateEmailConfirmDTO accountsUpdateEmailConfirmDTO,
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
        accountsUpdateEmailConfirmUseCase.execute(
            userIp,
            userAgent,
            locale,
            accountsUpdateEmailConfirmDTO
        );

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.ACCOUNTS_UPDATE_EMAIL_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.ACCOUNTS_UPDATE_EMAIL_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.ACCOUNTS_UPDATE_EMAIL_SUCCESSFULLY.getMessageCode())
            .build()
        );

    }

}