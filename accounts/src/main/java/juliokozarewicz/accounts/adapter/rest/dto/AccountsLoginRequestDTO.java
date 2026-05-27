package juliokozarewicz.accounts.adapter.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalExceptionEnum;
import juliokozarewicz.accounts.application.command.AccountsLoginRequestCommand;

public record AccountsLoginRequestDTO(

    @NotBlank(message = GlobalExceptionEnum.ACCOUNTS_FIELD_CANNOT_BE_EMPTY_DTO)
    @Email(message = GlobalExceptionEnum.ACCOUNTS_INVALID_EMAIL_DTO)
    @Size(max=256, message = GlobalExceptionEnum.ACCOUNTS_TOO_MANY_CHARACTERS_DTO)
    String email,

    @NotBlank(message = GlobalExceptionEnum.ACCOUNTS_FIELD_CANNOT_BE_EMPTY_DTO)
    @Size(min=12, message = GlobalExceptionEnum.ACCOUNTS_TOO_FEW_CHARACTERS_DTO)
    @Size(max=256, message = GlobalExceptionEnum.ACCOUNTS_TOO_MANY_CHARACTERS_DTO)
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$",
        message = GlobalExceptionEnum.ACCOUNTS_PASSWORD_REQUIREMENTS_DTO
    )
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.ACCOUNTS_FORBIDDEN_CHARACTERS_DTO
    )
    String password

) implements AccountsLoginRequestCommand {

    @Override
    public char[] userPassword() {
        char[] chars = password.toCharArray();
        return chars;
    }

}