package juliokozarewicz.tasks.adapter.rest.dto;

import jakarta.validation.constraints.*;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalExceptionEnum;
import juliokozarewicz.tasks.application.command.TasksCreateUpdateCommand;

import java.time.LocalDateTime;
import java.util.UUID;

public record TasksCreateUpadateDTO(

    @NotBlank(message = GlobalExceptionEnum.FIELD_CANNOT_BE_EMPTY_DTO)
    @Size(min = 3, message = GlobalExceptionEnum.TOO_FEW_CHARACTERS_DTO)
    @Size(max = 255, message = GlobalExceptionEnum.TOO_MANY_CHARACTERS_DTO)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS_DTO
    )
    String taskName,

    @Size(min = 1, message = GlobalExceptionEnum.FIELD_CANNOT_BE_EMPTY_DTO)
    @Size(max = 1000, message = GlobalExceptionEnum.TOO_MANY_CHARACTERS_DTO)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS_DTO
    )
    String description,

    UUID category,

    @Size(min = 1, message = GlobalExceptionEnum.FIELD_CANNOT_BE_EMPTY_DTO)
    @Size(max = 20, message = GlobalExceptionEnum.TOO_MANY_CHARACTERS_DTO)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS_DTO
    )
    String color,

    @Min(value = 1, message = GlobalExceptionEnum.INVALID_PRIORITY_DTO)
    @Max(value = 5, message = GlobalExceptionEnum.INVALID_PRIORITY_DTO)
    Integer priority,

    LocalDateTime startTime,

    LocalDateTime endTime,

    @Size(min = 1, message = GlobalExceptionEnum.FIELD_CANNOT_BE_EMPTY_DTO)
    @Size(max = 255, message = GlobalExceptionEnum.TOO_MANY_CHARACTERS_DTO)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS_DTO
    )
    String location,

    @NotNull(message = GlobalExceptionEnum.FIELD_CANNOT_BE_EMPTY_DTO)
    Boolean allDay,

    LocalDateTime reminderTime,

    @NotNull(message = GlobalExceptionEnum.FIELD_CANNOT_BE_EMPTY_DTO)
    Boolean notifyActive,

    @NotBlank(message = GlobalExceptionEnum.FIELD_CANNOT_BE_EMPTY_DTO)
    @Size(max = 255, message = GlobalExceptionEnum.TOO_MANY_CHARACTERS_DTO)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS_DTO
    )
    String status,

    LocalDateTime dueDate

) implements TasksCreateUpdateCommand {}