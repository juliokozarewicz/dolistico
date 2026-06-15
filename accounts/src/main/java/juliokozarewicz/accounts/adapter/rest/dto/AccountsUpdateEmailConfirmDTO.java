package juliokozarewicz.accounts.adapter.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalExceptionEnum;
import juliokozarewicz.accounts.application.command.AccountsUpdateEmailConfirmCommand;

public record AccountsUpdateEmailConfirmDTO(

    @NotBlank(message = GlobalExceptionEnum.ACCOUNTS_FIELD_CANNOT_BE_EMPTY_DTO)
    @Pattern(
        regexp = "^[a-fA-F0-9]{126,1024}$",
        message = GlobalExceptionEnum.ACCOUNTS_INVALID_TOKEN_DTO
    )
    String token,

    @NotBlank(message = GlobalExceptionEnum.ACCOUNTS_FIELD_CANNOT_BE_EMPTY_DTO)
    @Pattern(
        regexp = "^\\d{6}$",
        message = GlobalExceptionEnum.ACCOUNTS_INVALID_PIN_DTO
    )
    String pin

) implements AccountsUpdateEmailConfirmCommand {}