package juliokozarewicz.tasks.domain.repository;

import juliokozarewicz.tasks.domain.entity.TasksEntity;

import java.time.LocalDate;

public interface TasksRepository {

    boolean existsByTaskNameAndDueDate(String taskName, LocalDate dueDate);
    TasksEntity save(TasksEntity task);

}