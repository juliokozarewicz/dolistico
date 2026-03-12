package juliokozarewicz.tasks.application.command;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TasksGetCommand {

    String taskName();
    UUID category();
    Integer priority();
    String location();
    String status();
    LocalDateTime dueDate();

}