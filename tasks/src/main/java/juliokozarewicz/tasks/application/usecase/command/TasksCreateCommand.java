package juliokozarewicz.tasks.application.usecase.command;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TasksCreateCommand (

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
    LocalDate dueDate

) {}