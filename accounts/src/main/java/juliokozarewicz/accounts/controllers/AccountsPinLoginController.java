package juliokozarewicz.accounts.controllers;

import jakarta.validation.Valid;
import juliokozarewicz.accounts.dtos.AccountsPinLoginDTO;
import juliokozarewicz.accounts.services.AccountsPinLoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
class AccountsPinLoginController {

    // ==================================================== ( constructor init )

    private final AccountsPinLoginService accountsPinLoginService;

    public AccountsPinLoginController(
        AccountsPinLoginService accountsPinLoginService
    ) {
        this.accountsPinLoginService = accountsPinLoginService;
    }
    // ===================================================== ( constructor end )

    @PostMapping("/${ACCOUNTS_BASE_URL}/login-pin")
    public ResponseEntity handle(

        // dtos errors
        @Valid @RequestBody
        AccountsPinLoginDTO accountsPinLoginDTO,

        BindingResult bindingResult

    ) {

        return accountsPinLoginService.execute(
            accountsPinLoginDTO
        );

    }

}