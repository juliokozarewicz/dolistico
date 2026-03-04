package juliokozarewicz.tasks.domain.repository;

import juliokozarewicz.tasks.domain.entity.TasksEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface TasksRepository {

    boolean existsByTaskNameAndDueDate(String taskName, LocalDateTime dueDate);
    void save(TasksEntity task);

}