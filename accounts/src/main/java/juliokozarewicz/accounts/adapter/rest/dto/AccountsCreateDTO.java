package juliokozarewicz.accounts.adapter.rest.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalExceptionEnum;
import juliokozarewicz.accounts.application.command.AccountsCreateCommand;

public record AccountsCreateDTO(

    @Size(min = 1, message = GlobalExceptionEnum.ACCOUNTS_FIELD_CANNOT_BE_EMPTY_DTO)
    @Size(max = 256, message = GlobalExceptionEnum.ACCOUNTS_TOO_MANY_CHARACTERS_DTO)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.ACCOUNTS_FORBIDDEN_CHARACTERS_DTO
    )
    String message

) implements AccountsCreateCommand {}