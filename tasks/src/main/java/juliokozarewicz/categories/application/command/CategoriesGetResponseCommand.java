package juliokozarewicz.categories.application.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoriesGetResponseCommand(

    UUID idCreated,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String categoryName

) {}