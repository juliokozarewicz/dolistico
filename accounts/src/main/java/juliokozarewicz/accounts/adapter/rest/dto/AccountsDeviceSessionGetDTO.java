package juliokozarewicz.accounts.adapter.rest.dto;

import jakarta.validation.constraints.Max;
import juliokozarewicz.accounts.adapter.rest.enums.GlobalExceptionEnum;
import juliokozarewicz.accounts.application.command.AccountsDeviceSessionGetCommand;

public record AccountsDeviceSessionGetDTO (

    @Max(value = 10000, message = GlobalExceptionEnum.ACCOUNTS_INVALID_PAGE_NUMBER)
    Integer pageNumber

) implements AccountsDeviceSessionGetCommand {}
