package juliokozarewicz.accounts.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import juliokozarewicz.accounts.adapter.rest.dto.AccountsLoginConfirmDTO;
import juliokozarewicz.accounts.adapter.rest.dto.AccountsRequestDTO;
import juliokozarewicz.accounts.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.accounts.application.usecase.AccountsLoginConfirmUseCase;
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
import java.util.Optional;

@RestController
@Validated
@RequestMapping("${ACCOUNTS_BASE_URL}")
public class AccountsLoginConfirmController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsLoginConfirmUseCase accountsLoginConfirmUseCase;
    private final AccountsRequestDTO accountsRequestDTO;

    public AccountsLoginConfirmController(

        AccountsLoginConfirmUseCase accountsLoginConfirmUseCase,
        AccountsRequestDTO accountsRequestDTO

    ) {

        this.accountsLoginConfirmUseCase = accountsLoginConfirmUseCase;
        this.accountsRequestDTO = accountsRequestDTO;

    }

    // ===================================================== ( constructor end )

    @PostMapping("/login/confirm")
    public ResponseEntity<StandardResponseDTO> handle (

        // Locale from Accept-Language
        Locale locale,

        // DTO error
        @Valid @RequestBody AccountsLoginConfirmDTO accountsLoginConfirmDTO,
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
        Map<String, Object> credentials = accountsLoginConfirmUseCase.execute(
            "userIp",
            "userAgent",
            locale,
            accountsLoginConfirmDTO
        );

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.ACCOUNTS_LOGIN_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.ACCOUNTS_LOGIN_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.ACCOUNTS_LOGIN_SUCCESSFULLY.getMessageCode())
            .data(credentials)
            .build()
        );

    }

}