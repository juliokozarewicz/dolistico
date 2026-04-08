package juliokozarewicz.categories.adapter.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import juliokozarewicz.categories.application.command.CategoriesCreateUpdateCommand;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalExceptionEnum;

public record CategoriesCreateUpadateDTO (

    @NotBlank(message = GlobalExceptionEnum.FIELD_CANNOT_BE_EMPTY)
    @Size(max = 255, message = GlobalExceptionEnum.TOO_MANY_CHARACTERS)
    @Pattern(
        regexp = "^[^<>&'\"/]*$",
        message = GlobalExceptionEnum.FORBIDDEN_CHARACTERS
    )
    String categoryName

) implements CategoriesCreateUpdateCommand {}