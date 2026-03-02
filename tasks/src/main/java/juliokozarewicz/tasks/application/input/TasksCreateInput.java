package juliokozarewicz.tasks.application.input;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TasksCreateInput (

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