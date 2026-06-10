package juliokozarewicz.accounts.adapter.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalExceptionEnum;
import juliokozarewicz.accounts.application.command.AccountsLogoutUserCommand;

public record AccountsLogoutUserDTO(

    @NotBlank(message = GlobalExceptionEnum.ACCOUNTS_FIELD_CANNOT_BE_EMPTY_DTO)
    @Pattern(
        regexp = "^[A-Za-z0-9_-]{128,2048}$",
        message = GlobalExceptionEnum.ACCOUNTS_INVALID_TOKEN_DTO
    )
    String refreshToken

) implements AccountsLogoutUserCommand {}