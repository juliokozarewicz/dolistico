package juliokozarewicz.tasks.adapter.rest.dto;

import jakarta.validation.constraints.*;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalExceptionEnum;
import juliokozarewicz.tasks.application.command.TasksGetCommand;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TasksGetDTO(

    @Min(value = 1, message = GlobalExceptionEnum.TOO_FEW_CHARACTERS_DTO)
    @Max(value = 100, message = GlobalExceptionEnum.TOO_MANY_CHARACTERS_DTO)
    Integer pageSize,

    @Max(value = 100, message = GlobalExceptionEnum.TOO_MANY_CHARACTERS_DTO)
    Integer pageNumber,

    @Size(min = 3, message = GlobalExceptionEnum.TOO_FEW_CHARACTERS_DTO)
    @Size(max = 255, message = GlobalExceptionEnum.TOO_MANY_CHARACTERS_DTO)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS_DTO
    )
    String taskName,

    String category,

    @Min(value = 1, message = GlobalExceptionEnum.INVALID_PRIORITY_DTO)
    @Max(value = 5, message = GlobalExceptionEnum.INVALID_PRIORITY_DTO)
    Integer priority,

    @Size(min = 1, message = GlobalExceptionEnum.TOO_FEW_CHARACTERS_DTO)
    @Size(max = 255, message = GlobalExceptionEnum.TOO_MANY_CHARACTERS_DTO)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS_DTO
    )
    String location,

    @Size(min = 1, message = GlobalExceptionEnum.TOO_FEW_CHARACTERS_DTO)
    @Size(max = 255, message = GlobalExceptionEnum.TOO_MANY_CHARACTERS_DTO)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS_DTO
    )
    String status,

    LocalDate dueDateInit,

    LocalDate dueDateEnd

) implements TasksGetCommand {}