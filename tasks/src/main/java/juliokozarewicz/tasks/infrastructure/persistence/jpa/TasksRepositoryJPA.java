package juliokozarewicz.tasks.infrastructure.persistence.jpa;

import juliokozarewicz.tasks.infrastructure.persistence.model.TasksModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TasksRepositoryJPA extends
    JpaRepository<TasksModel, UUID>, JpaSpecificationExecutor<TasksModel> {

    boolean existsByIdUserAndTaskNameAndDueDate(
        UUID idUser,
        String taskName,
        LocalDateTime dueDate
    );

    Optional<TasksModel> findByIdAndIdUser(UUID id, UUID idUser);

    boolean existsByIdUserAndTaskNameAndDueDateAndIdNot(
        UUID idUser,
        String taskName,
        LocalDateTime dueDate,
        UUID id
    );

    @Modifying
    @Transactional
    void deleteByIdAndIdUser(UUID id, UUID idUser);

}