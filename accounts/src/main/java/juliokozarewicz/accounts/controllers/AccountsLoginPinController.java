package juliokozarewicz.accounts.controllers;

import jakarta.validation.Valid;
import juliokozarewicz.accounts.dtos.AccountsLoginPinDTO;
import juliokozarewicz.accounts.services.AccountsLoginPinService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
class AccountsLoginPinController {

    // ==================================================== ( constructor init )

    private final AccountsLoginPinService accountsLoginPinService;

    public AccountsLoginPinController(
        AccountsLoginPinService accountsLoginPinService
    ) {
        this.accountsLoginPinService = accountsLoginPinService;
    }
    // ===================================================== ( constructor end )

    @PostMapping("/${ACCOUNTS_BASE_URL}/login-pin")
    public ResponseEntity handle(

        // dtos errors
        @Valid @RequestBody
        AccountsLoginPinDTO accountsLoginPinDTO,

        BindingResult bindingResult

    ) {

        return accountsLoginPinService.execute(
            accountsLoginPinDTO
        );

    }

}