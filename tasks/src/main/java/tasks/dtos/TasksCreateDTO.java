package tasks.dtos;

import jakarta.validation.constraints.Size;

public record TasksCreateDTO(

    @Size(min = 1, message = "{validation_not_empty}")
    @Size(max = 5000, message = "{validation_many_characters}")
    String message

) {}