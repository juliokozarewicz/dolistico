package juliokozarewicz.tasks.infrastructure.persistence.repositoryimpl;

import juliokozarewicz.tasks.infrastructure.persistence.model.TasksModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface TasksRepositoryImpl extends JpaRepository<TasksModel, UUID> {

    boolean existsByTaskNameAndDueDate(String taskName, LocalDate dueDate);

}