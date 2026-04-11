package juliokozarewicz.tasks.application.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TasksGetCommand {

    Integer pageSize();
    Integer pageNumber();
    String taskName();
    String category();
    Integer priority();
    String location();
    String status();
    LocalDate dueDateInit();
    LocalDate dueDateEnd();

}