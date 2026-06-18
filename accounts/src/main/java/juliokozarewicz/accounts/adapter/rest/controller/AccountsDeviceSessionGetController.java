package juliokozarewicz.accounts.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import juliokozarewicz.accounts.adapter.rest.dto.AccountsDeviceSessionGetDTO;
import juliokozarewicz.accounts.adapter.rest.dto.AccountsLoginConfirmDTO;
import juliokozarewicz.accounts.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.accounts.application.usecase.AccountsDeviceSessionGetUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("${ACCOUNTS_BASE_URL}")
public class AccountsDeviceSessionGetController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsDeviceSessionGetUseCase accountsDeviceSessionGetUseCase;

    public AccountsDeviceSessionGetController(

        AccountsDeviceSessionGetUseCase accountsDeviceSessionGetUseCase

    ) {

        this.accountsDeviceSessionGetUseCase = accountsDeviceSessionGetUseCase;

    }

    // ===================================================== ( constructor end )

    @GetMapping("/devices")
    public ResponseEntity<StandardResponseDTO> handle (

        // DTO error
        @Valid AccountsDeviceSessionGetDTO accountsDeviceSessionGetDTO,
        BindingResult bindingResult
    ) {

        // Data for auth
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID idUser = UUID.fromString(jwt.getSubject());

        // Call use case
        Map<String, Object> userDevices = accountsDeviceSessionGetUseCase.execute(
            idUser,
            accountsDeviceSessionGetDTO
        );

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.ACCOUNTS_GET_DEVICE_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.ACCOUNTS_GET_DEVICE_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.ACCOUNTS_GET_DEVICE_SUCCESSFULLY.getMessageCode())
            .data(userDevices)
            .build()
        );

    }

}