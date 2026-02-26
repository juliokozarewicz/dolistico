package juliokozarewicz.helloworld.presentation.rest.dto;

import jakarta.validation.constraints.Size;
import juliokozarewicz.helloworld.presentation.rest.exception.GlobalExceptionEnum;

public record HelloWorldDTO (

    @Size(min = 1, message = GlobalExceptionEnum.FIELD_CANNOT_REMAIN_EMPTY)
    @Size(max = 256, message = GlobalExceptionEnum.MANY_CHARACTERS)
    String message

) {}