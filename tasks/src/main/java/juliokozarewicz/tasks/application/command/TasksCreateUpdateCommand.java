package juliokozarewicz.tasks.application.command;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TasksCreateUpdateCommand {

    String taskName();
    String description();
    UUID category();
    String color();
    Integer priority();
    LocalDateTime startTime();
    LocalDateTime endTime();
    String location();
    Boolean allDay();
    LocalDateTime reminderTime();
    Boolean notifyActive();
    String status();
    LocalDateTime dueDate();

}