package juliokozarewicz.accounts.adapter.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalExceptionEnum;
import juliokozarewicz.accounts.application.command.AccountsUpdatePasswordRequestCommand;

public record AccountsUpdatePasswordRequestDTO(

    @NotBlank(message = GlobalExceptionEnum.ACCOUNTS_FIELD_CANNOT_BE_EMPTY_DTO)
    @Email(message = GlobalExceptionEnum.ACCOUNTS_INVALID_EMAIL_DTO)
    @Size(max=256, message = GlobalExceptionEnum.ACCOUNTS_TOO_MANY_CHARACTERS_DTO)
    String email

) implements AccountsUpdatePasswordRequestCommand {}