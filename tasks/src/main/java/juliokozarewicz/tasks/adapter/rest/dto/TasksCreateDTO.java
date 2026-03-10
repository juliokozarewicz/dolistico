package juliokozarewicz.tasks.adapter.rest.dto;

import jakarta.validation.constraints.*;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalExceptionEnum;
import juliokozarewicz.tasks.application.command.TasksCreateCommand;

import java.time.LocalDateTime;
import java.util.UUID;

public record TasksCreateDTO(

    @NotBlank(message = GlobalExceptionEnum.FIELD_CANNOT_REMAIN_EMPTY)
    @Size(min = 3, message = GlobalExceptionEnum.FEW_CHARACTERS)
    @Size(max = 255, message = GlobalExceptionEnum.MANY_CHARACTERS)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String taskName,

    @Size(min = 1, message = GlobalExceptionEnum.FIELD_CANNOT_REMAIN_EMPTY)
    @Size(max = 1000, message = GlobalExceptionEnum.MANY_CHARACTERS)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String description,

    UUID category,

    @Size(min = 1, message = GlobalExceptionEnum.FIELD_CANNOT_REMAIN_EMPTY)
    @Size(max = 20, message = GlobalExceptionEnum.MANY_CHARACTERS)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String color,

    @Min(value = 1, message = GlobalExceptionEnum.INVALID_PRIORITY_DTO)
    @Max(value = 5, message = GlobalExceptionEnum.INVALID_PRIORITY_DTO)
    Integer priority,

    LocalDateTime startTime,

    LocalDateTime endTime,

    @Size(min = 1, message = GlobalExceptionEnum.FIELD_CANNOT_REMAIN_EMPTY)
    @Size(max = 255, message = GlobalExceptionEnum.MANY_CHARACTERS)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String location,

    @NotNull(message = GlobalExceptionEnum.FIELD_CANNOT_REMAIN_EMPTY)
    Boolean allDay,

    LocalDateTime reminderTime,

    @NotNull(message = GlobalExceptionEnum.FIELD_CANNOT_REMAIN_EMPTY)
    Boolean notifyActive,

    @NotBlank(message = GlobalExceptionEnum.FIELD_CANNOT_REMAIN_EMPTY)
    @Size(max = 255, message = GlobalExceptionEnum.MANY_CHARACTERS)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String status,

    LocalDateTime dueDate

) implements TasksCreateCommand {}