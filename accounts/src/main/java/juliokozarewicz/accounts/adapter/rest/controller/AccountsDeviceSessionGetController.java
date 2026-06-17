package juliokozarewicz.accounts.adapter.rest.controller;

import juliokozarewicz.accounts.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.accounts.application.usecase.AccountsProfileUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    private final AccountsProfileUseCase accountsProfileUseCase;

    public AccountsDeviceSessionGetController(

        AccountsProfileUseCase accountsProfileUseCase

    ) {

        this.accountsProfileUseCase = accountsProfileUseCase;

    }

    // ===================================================== ( constructor end )

    @GetMapping("/devices")
    public ResponseEntity<StandardResponseDTO> handle () {

        // Data for auth
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID idUser = UUID.fromString(jwt.getSubject());

        // Call use case
        Map<String, Object> userDevices = accountsProfileUseCase.execute(idUser);

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