package juliokozarewicz.tasks.infrastructure.persistence.jpa;

import juliokozarewicz.tasks.infrastructure.persistence.model.TasksModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TasksRepositoryJPA extends JpaRepository<TasksModel, UUID> {

    boolean existsByTaskNameAndDueDate(String taskName, LocalDateTime dueDate);

    List<TasksModel> findAllByIdUser(UUID idUser);

}