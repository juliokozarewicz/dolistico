package juliokozarewicz.accounts.controllers;

import juliokozarewicz.accounts.dtos.AccountsLinkUpdateEmailDTO;
import juliokozarewicz.accounts.services.AccountsLinkUpdateEmailService;
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
class AccountsLinkUpdateEmailController {

    // ==================================================== ( constructor init )

    private final AccountsLinkUpdateEmailService accountsLinkUpdateEmailService;

    public AccountsLinkUpdateEmailController(

        AccountsLinkUpdateEmailService accountsLinkUpdateEmailService

    ) {

        this.accountsLinkUpdateEmailService = accountsLinkUpdateEmailService;

    }

    // ===================================================== ( constructor end )

    @PostMapping("/${ACCOUNTS_BASE_URL}/update-email-link")
    @SuppressWarnings("unchecked")
    public ResponseEntity handle(

        // dtos errors
        @Valid @RequestBody AccountsLinkUpdateEmailDTO accountsLinkUpdateEmailDTO,

        BindingResult bindingResult,

        HttpServletRequest request

    ) {

        // Auth endpoint
        Map<String, Object> credentialsData = (Map<String, Object>)
        request.getAttribute("credentialsData");

        return accountsLinkUpdateEmailService.execute(
            credentialsData,
            accountsLinkUpdateEmailDTO
        );

    }

}