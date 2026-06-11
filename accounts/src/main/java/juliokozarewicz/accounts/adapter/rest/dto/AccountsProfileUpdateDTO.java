package juliokozarewicz.accounts.adapter.rest.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalExceptionEnum;
import juliokozarewicz.accounts.application.command.AccountsPofileUpdateCommand;

public record AccountsProfileUpdateDTO (

    @Size(min = 3, message = GlobalExceptionEnum.ACCOUNTS_TOO_FEW_CHARACTERS_DTO)
    @Size(max = 256, message = GlobalExceptionEnum.ACCOUNTS_TOO_MANY_CHARACTERS_DTO)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.ACCOUNTS_FORBIDDEN_CHARACTERS_DTO
    )
    String fullName,

    @Size(min = 1, message = GlobalExceptionEnum.ACCOUNTS_TOO_FEW_CHARACTERS_DTO)
    @Size(max = 25, message = GlobalExceptionEnum.ACCOUNTS_TOO_MANY_CHARACTERS_DTO)
    @Pattern(regexp = "^[+\\d()\\s-]*$", message = GlobalExceptionEnum.ACCOUNTS_FORBIDDEN_CHARACTERS_DTO)
    String phone,

    @Size(min = 1, message = GlobalExceptionEnum.ACCOUNTS_TOO_FEW_CHARACTERS_DTO)
    @Size(max = 256, message = GlobalExceptionEnum.ACCOUNTS_TOO_MANY_CHARACTERS_DTO)
    @Pattern(regexp = "^[^<>&'\"/]*$", message = GlobalExceptionEnum.ACCOUNTS_FORBIDDEN_CHARACTERS_DTO)
    String identityDocument,

    @Size(min = 1, message = GlobalExceptionEnum.ACCOUNTS_TOO_FEW_CHARACTERS_DTO)
    @Size(max = 256, message = GlobalExceptionEnum.ACCOUNTS_TOO_MANY_CHARACTERS_DTO)
    @Pattern(regexp = "^[^<>&'\"/]*$", message = GlobalExceptionEnum.ACCOUNTS_FORBIDDEN_CHARACTERS_DTO)
    String gender,

    @Size(min = 1, message = GlobalExceptionEnum.ACCOUNTS_TOO_FEW_CHARACTERS_DTO)
    @Pattern(
        regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
        message = GlobalExceptionEnum.ACCOUNTS_INVALID_DATE_DTO
    )
    String birthdate,

    @Size(min = 1, message = GlobalExceptionEnum.ACCOUNTS_TOO_FEW_CHARACTERS_DTO)
    @Size(max = 256, message = GlobalExceptionEnum.ACCOUNTS_TOO_MANY_CHARACTERS_DTO)
    @Pattern(regexp = "^[^<>&'\"/]*$", message = GlobalExceptionEnum.ACCOUNTS_FORBIDDEN_CHARACTERS_DTO)
    String biography,

    @Size(min = 1, message = GlobalExceptionEnum.ACCOUNTS_TOO_FEW_CHARACTERS_DTO)
    @Size(max = 50, message = GlobalExceptionEnum.ACCOUNTS_TOO_MANY_CHARACTERS_DTO)
    @Pattern(regexp = "^[a-zA-Z-]*$", message = GlobalExceptionEnum.ACCOUNTS_FORBIDDEN_CHARACTERS_DTO)
    String language,

    @Size(min = 1, message = GlobalExceptionEnum.ACCOUNTS_TOO_FEW_CHARACTERS_DTO)
    @Size(max = 100, message = GlobalExceptionEnum.ACCOUNTS_TOO_MANY_CHARACTERS_DTO)
    @Pattern(regexp = "^[a-zA-Z-]*$", message = GlobalExceptionEnum.ACCOUNTS_FORBIDDEN_CHARACTERS_DTO)
    String theme

) implements AccountsPofileUpdateCommand {}