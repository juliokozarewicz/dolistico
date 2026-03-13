package juliokozarewicz.tasks.application.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record TasksGetResponseCommand(

    UUID idCreated,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String taskName,
    String description,
    CategoryCommand category,
    String color,
    Integer priority,
    LocalDateTime startTime,
    LocalDateTime endTime,
    String location,
    boolean allDay,
    LocalDateTime reminderTime,
    boolean notifyActive,
    String status,
    LocalDateTime dueDate

) {

    public record CategoryCommand(
        UUID id,
        String categoryName
    ) {}

}