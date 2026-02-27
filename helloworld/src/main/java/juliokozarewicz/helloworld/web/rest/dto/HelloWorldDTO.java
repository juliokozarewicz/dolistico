package juliokozarewicz.helloworld.web.rest.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import juliokozarewicz.helloworld.web.rest.exception.GlobalExceptionEnum;

public record HelloWorldDTO (

    @Size(min = 1, message = GlobalExceptionEnum.FIELD_CANNOT_REMAIN_EMPTY)
    @Size(max = 256, message = GlobalExceptionEnum.MANY_CHARACTERS)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String message

) {}