package juliokozarewicz.tasks.domain.repository;

import juliokozarewicz.tasks.domain.entity.TasksEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TasksRepository {

    boolean existsByTaskNameAndDueDate(String taskName, LocalDateTime dueDate);

    List<TasksEntity> findAllByIdUser(

        UUID idUser,
        String taskName,
        String category,
        Integer priority,
        String location,
        String status,
        LocalDate dueDateInit,
        LocalDate dueDateEnd

    );

    void save(TasksEntity task);

}