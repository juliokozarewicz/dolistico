package juliokozarewicz.accounts.adapter.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import juliokozarewicz.accounts.adapter.rest.dto.StandardResponseDTO;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalSuccessEnum;
import juliokozarewicz.accounts.application.usecase.AccountsAvatarUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("${ACCOUNTS_BASE_URL}")
public class AccountsAvatarController {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final AccountsAvatarUseCase accountsAvatarUseCase;

    public AccountsAvatarController(

        AccountsAvatarUseCase accountsAvatarUseCase

    ) {

        this.accountsAvatarUseCase = accountsAvatarUseCase;

    }

    // ===================================================== ( constructor end )

    @PutMapping("/profile/avatar")
    public ResponseEntity<StandardResponseDTO> handle (

        // Request for auth
        @RequestParam(value = "avatar", required = false) MultipartFile[] file

    ) throws IOException {

        // Data for auth
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID idUser = UUID.fromString(jwt.getSubject());

        // Call use case
        Map<String, Object> avatarUpdated = accountsAvatarUseCase.execute(idUser, file);

        // Standard response
        return ResponseEntity
        .status(GlobalSuccessEnum.ACCOUNTS_AVATAR_SUCCESSFULLY.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new StandardResponseDTO.Builder()
            .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
            .statusCode(GlobalSuccessEnum.ACCOUNTS_AVATAR_SUCCESSFULLY.getStatusCode())
            .messageCode(GlobalSuccessEnum.ACCOUNTS_AVATAR_SUCCESSFULLY.getMessageCode())
            .data(avatarUpdated)
            .build()
        );

    }

}