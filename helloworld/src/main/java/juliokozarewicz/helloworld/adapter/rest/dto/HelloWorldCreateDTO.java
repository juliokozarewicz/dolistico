package juliokozarewicz.helloworld.adapter.rest.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import juliokozarewicz.helloworld.adapter.rest.enums.GlobalExceptionEnum;
import juliokozarewicz.helloworld.application.command.HelloWorldCreateCommand;

public record HelloWorldCreateDTO(

    @Size(min = 1, message = GlobalExceptionEnum.TASKS_FIELD_CANNOT_BE_EMPTY_DTO)
    @Size(max = 256, message = GlobalExceptionEnum.TASKS_TOO_MANY_CHARACTERS_DTO)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.TASKS_FORBIDDEN_CHARACTERS_DTO
    )
    String message

) implements HelloWorldCreateCommand {}