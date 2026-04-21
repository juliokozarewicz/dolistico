package juliokozarewicz.accounts.controllers;

import juliokozarewicz.accounts.dtos.AccountsAddressCreateDTO;
import juliokozarewicz.accounts.services.AccountsAddressCreateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Validated
class AccountsAddressCreateController {

    // ==================================================== ( constructor init )
    
    private final AccountsAddressCreateService accountsAddressCreateService;

    public AccountsAddressCreateController(

        AccountsAddressCreateService accountsAddressCreateService

    ) {

        this.accountsAddressCreateService = accountsAddressCreateService;

    }

    // ===================================================== ( constructor end )

    @PostMapping("/${ACCOUNTS_BASE_URL}/create-address")
    @SuppressWarnings("unchecked")
    public ResponseEntity handle(

        @Valid @RequestBody AccountsAddressCreateDTO accountsAddressCreateDTO,
        BindingResult bindingResult,
        HttpServletRequest request

    ) {

        // Auth endpoint
        Map<String, Object> credentialsData = (Map<String, Object>)
        request.getAttribute("credentialsData");

        return accountsAddressCreateService.execute(
            credentialsData,
            accountsAddressCreateDTO
        );

    }

}