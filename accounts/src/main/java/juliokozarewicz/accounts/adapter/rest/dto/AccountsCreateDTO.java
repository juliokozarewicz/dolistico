package juliokozarewicz.accounts.adapter.rest.dto;

import jakarta.validation.constraints.*;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalExceptionEnum;
import juliokozarewicz.accounts.application.command.AccountsCreateCommand;

public record AccountsCreateDTO(

    @NotBlank(message = GlobalExceptionEnum.ACCOUNTS_FIELD_CANNOT_BE_EMPTY_DTO)
    @Size(min=3, max=256, message = GlobalExceptionEnum.ACCOUNTS_TOO_MANY_CHARACTERS_DTO)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.ACCOUNTS_FORBIDDEN_CHARACTERS_DTO
    )
    String fullName,

    @NotBlank(message = GlobalExceptionEnum.ACCOUNTS_FIELD_CANNOT_BE_EMPTY_DTO)
    @Email(message = GlobalExceptionEnum.ACCOUNTS_FORBIDDEN_CHARACTERS_DTO)
    @Size(max=256, message = GlobalExceptionEnum.ACCOUNTS_TOO_MANY_CHARACTERS_DTO)
    String email,

    @NotBlank(message = GlobalExceptionEnum.ACCOUNTS_FIELD_CANNOT_BE_EMPTY_DTO)
    @Size(min=12, message = GlobalExceptionEnum.ACCOUNTS_TOO_FEW_CHARACTERS_DTO)
    @Size(max=128, message = GlobalExceptionEnum.ACCOUNTS_TOO_MANY_CHARACTERS_DTO)
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$",
        message = GlobalExceptionEnum.ACCOUNTS_PASSWORD_REQUIREMENTS_DTO
    )
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.ACCOUNTS_FORBIDDEN_CHARACTERS_DTO
    )
    char[] password

) implements AccountsCreateCommand {

    @Override
    public char[] password() {
        return password.clone();
    }

}