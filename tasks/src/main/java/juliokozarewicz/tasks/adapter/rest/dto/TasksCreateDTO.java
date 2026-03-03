package juliokozarewicz.tasks.adapter.rest.dto;

import jakarta.validation.constraints.*;
import juliokozarewicz.tasks.adapter.rest.exception.GlobalExceptionEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Size(min = 1, message = GlobalExceptionEnum.FIELD_CANNOT_REMAIN_EMPTY)
    @Size(max = 255, message = GlobalExceptionEnum.MANY_CHARACTERS)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String category,

    @Size(min = 1, message = GlobalExceptionEnum.FIELD_CANNOT_REMAIN_EMPTY)
    @Size(max = 20, message = GlobalExceptionEnum.MANY_CHARACTERS)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String color,

    @Min(value = 1, message = GlobalExceptionEnum.FEW_CHARACTERS)
    @Max(value = 5, message = GlobalExceptionEnum.MANY_CHARACTERS)
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

    LocalDate dueDate

) {}