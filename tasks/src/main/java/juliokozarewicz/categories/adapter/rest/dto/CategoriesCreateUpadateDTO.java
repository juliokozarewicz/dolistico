package juliokozarewicz.categories.adapter.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import juliokozarewicz.categories.application.command.CategoriesCreateUpdateCommand;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalExceptionEnum;

public record CategoriesCreateUpadateDTO (

    @NotBlank(message = GlobalExceptionEnum.TASKS_FIELD_CANNOT_BE_EMPTY_DTO)
    @Size(min = 3, message = GlobalExceptionEnum.TASKS_TOO_FEW_CHARACTERS_DTO)
    @Size(max = 255, message = GlobalExceptionEnum.TASKS_TOO_MANY_CHARACTERS_DTO)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.TASKS_FORBIDDEN_CHARACTERS_DTO
    )
    String categoryName

) implements CategoriesCreateUpdateCommand {}