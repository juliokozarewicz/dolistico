
package juliokozarewicz.categories.adapter.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import juliokozarewicz.tasks.adapter.rest.enums.GlobalExceptionEnum;

public record ValidationIdentityDTO(

    @NotBlank(message = GlobalExceptionEnum.TASKS_FIELD_CANNOT_BE_EMPTY_DTO)
    @Pattern(
        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
        message = GlobalExceptionEnum.TASKS_INVALID_ID_DTO
    )
    String id

) {}