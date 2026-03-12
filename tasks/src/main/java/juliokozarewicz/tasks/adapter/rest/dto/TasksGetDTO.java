package juliokozarewicz.tasks.adapter.rest.dto;

import jakarta.validation.constraints.*;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalExceptionEnum;
import juliokozarewicz.tasks.application.command.TasksGetCommand;

import java.time.LocalDateTime;
import java.util.UUID;

public record TasksGetDTO(

    @NotBlank(message = GlobalExceptionEnum.FIELD_CANNOT_REMAIN_EMPTY)
    @Size(min = 3, message = GlobalExceptionEnum.FEW_CHARACTERS)
    @Size(max = 255, message = GlobalExceptionEnum.MANY_CHARACTERS)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String taskName,

    UUID category,

    @Min(value = 1, message = GlobalExceptionEnum.INVALID_PRIORITY_DTO)
    @Max(value = 5, message = GlobalExceptionEnum.INVALID_PRIORITY_DTO)
    Integer priority,

    @Size(min = 1, message = GlobalExceptionEnum.FIELD_CANNOT_REMAIN_EMPTY)
    @Size(max = 255, message = GlobalExceptionEnum.MANY_CHARACTERS)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String location,

    @NotBlank(message = GlobalExceptionEnum.FIELD_CANNOT_REMAIN_EMPTY)
    @Size(max = 255, message = GlobalExceptionEnum.MANY_CHARACTERS)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String status,

    LocalDateTime dueDate

) implements TasksGetCommand {}