package juliokozarewicz.tasks.adapter.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import juliokozarewicz.tasks.adapter.rest.exception.GlobalExceptionEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TasksCreateDTO(

    @NotNull
    @Size(min = 1, max = 255)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String taskName,

    @Size(max = 1000)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String description,

    @Size(max = 255)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String category,

    @Size(max = 20)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String color,

    @NotNull
    Integer priority,

    LocalDateTime startTime,

    LocalDateTime endTime,

    @Size(max = 255)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String location,

    @NotNull
    Boolean allDay,

    LocalDateTime reminderTime,

    @NotNull
    Boolean notifyActive,

    @NotNull
    @Size(min = 1, max = 255)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String status,

    LocalDate dueDate

) {}