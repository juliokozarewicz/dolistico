package juliokozarewicz.helloworld.present.rest.dto;

import jakarta.validation.constraints.Size;

public record HelloWorldDTO (

    @Size(min = 1, message = "FIELD_CANNOT_REMAIN_EMPTY")
    @Size(max = 256, message = "MANY_CHARACTERS")
    String message

) {}