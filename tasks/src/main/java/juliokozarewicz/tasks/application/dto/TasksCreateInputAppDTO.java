package juliokozarewicz.tasks.application.dto;

import java.time.LocalDateTime;

public record TasksCreateInputAppDTO(

    String taskName,
    String description,
    String category,
    String color,
    Integer priority,
    LocalDateTime startTime,
    LocalDateTime endTime,
    String location,
    Boolean allDay,
    LocalDateTime reminderTime,
    Boolean notifyActive,
    String status,
    LocalDateTime dueDate

) {}