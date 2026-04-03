package juliokozarewicz.tasks.domain.repository;

import juliokozarewicz.tasks.domain.entity.TasksEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface TasksRepository {

    boolean existsByIdUserAndTaskNameAndDueDate(
        UUID idUser,
        String taskName,
        LocalDateTime dueDate
    );

    Page<TasksEntity> findAllByIdUser(
        UUID idUser,
        String taskName,
        String category,
        Integer priority,
        String location,
        String status,
        LocalDate dueDateInit,
        LocalDate dueDateEnd,
        Pageable pageable
    );

    void save(TasksEntity task);

    Optional<TasksEntity> findById(UUID id);

    Optional<TasksEntity> findByIdAndUser(UUID taskId, UUID idUser);

    boolean existsByTaskNameAndDueDateAndIdNot(
        UUID idUser,
        String taskName,
        LocalDateTime dueDate,
        UUID excludeId
    );

    void update(TasksEntity task);

}