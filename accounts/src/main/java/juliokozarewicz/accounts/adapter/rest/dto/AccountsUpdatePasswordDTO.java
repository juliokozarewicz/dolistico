package juliokozarewicz.accounts.adapter.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalExceptionEnum;

public record AccountsUpdatePasswordDTO(

    @NotBlank(message = GlobalExceptionEnum.ACCOUNTS_FIELD_CANNOT_BE_EMPTY_DTO)
    @Size( min = 126, max = 1024, message = GlobalExceptionEnum.ACCOUNTS_INVALID_TOKEN_DTO)
    @Pattern(
        regexp = "^[a-fA-F0-9]+$",
        message = GlobalExceptionEnum.ACCOUNTS_FORBIDDEN_CHARACTERS_DTO
    )
    String token,

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

) {}