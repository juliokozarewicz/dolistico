package juliokozarewicz.tasks.infrastructure.persistence.jpa;

import juliokozarewicz.tasks.infrastructure.persistence.model.TasksModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface TasksRepositoryJPA extends JpaRepository<TasksModel, UUID> {

    boolean existsByTaskNameAndDueDate(String taskName, LocalDateTime dueDate);

}