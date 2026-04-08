package juliokozarewicz.helloworld.adapter.rest.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import juliokozarewicz.helloworld.adapter.rest.enums.GlobalExceptionEnum;
import juliokozarewicz.helloworld.application.command.HelloWorldCreateCommand;

public record HelloWorldCreateDTO(

    @Size(min = 1, message = GlobalExceptionEnum.FIELD_CANNOT_BE_EMPTY)
    @Size(max = 256, message = GlobalExceptionEnum.TOO_MANY_CHARACTERS)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String message

) implements HelloWorldCreateCommand {}