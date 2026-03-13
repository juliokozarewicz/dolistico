package juliokozarewicz.tasks.domain.repository;

import juliokozarewicz.tasks.domain.entity.TasksEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TasksRepository {

    boolean existsByTaskNameAndDueDate(String taskName, LocalDateTime dueDate);

    List<TasksEntity> findAllByUserId(UUID idUser);

    void save(TasksEntity task);

}