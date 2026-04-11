package juliokozarewicz.categories.adapter.rest.dto;

import jakarta.validation.constraints.*;
import juliokozarewicz.categories.application.command.CategoriesGetCommand;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalExceptionEnum;

public record CategoriesGetDTO (

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
    String categoryName

) implements CategoriesGetCommand {}