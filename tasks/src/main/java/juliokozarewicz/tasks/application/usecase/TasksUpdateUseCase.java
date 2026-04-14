package juliokozarewicz.tasks.application.usecase;

import juliokozarewicz.categories.domain.repository.CategoriesRepository;
import juliokozarewicz.tasks.application.command.TasksCreateUpdateCommand;
import juliokozarewicz.tasks.domain.entity.TasksEntity;
import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;
import juliokozarewicz.tasks.domain.repository.TasksRepository;
import juliokozarewicz.tasks.infrastructure.messaging.producer.TasksEventProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class TasksUpdateUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final TasksRepository tasksRepository;
    private final CategoriesRepository categoriesRepository;
    private final TasksEventProducer tasksEventProducer;

    public TasksUpdateUseCase(

        TasksRepository tasksRepository,
        CategoriesRepository categoriesRepository,
        TasksEventProducer tasksEventProducer

    ) {

        this.tasksRepository = tasksRepository;
        this.categoriesRepository = categoriesRepository;
        this.tasksEventProducer = tasksEventProducer;

    }

    // ===================================================== ( constructor end )

    @Transactional
    public void execute(

            Map<String, Object> credentialsData,
            UUID taskId,
            TasksCreateUpdateCommand tasksCreateUpdateCommand

    ) {

        // Credentials
        UUID idUser = UUID.fromString((String) credentialsData.get("id"));

        // Find existing task
        TasksEntity existingTask = tasksRepository.findByIdAndUser(taskId, idUser)
        .orElseThrow(() -> new DomainException(DomainExceptionEnum.TASK_NOT_FOUND));

        // Duplicated check (exclude current id)
        if ( tasksRepository.existsByTaskNameAndDueDateAndIdNot(
                idUser,
                tasksCreateUpdateCommand.taskName().toLowerCase().trim(),
                tasksCreateUpdateCommand.dueDate(),
                taskId
        )) {
            throw new DomainException(DomainExceptionEnum.DUPLICATED_TASK);
        }

        // New timestamp for update
        LocalDateTime timeStamp = LocalDateTime.now();

        // Verify category
        UUID idCategory = tasksCreateUpdateCommand.category();

        if (idCategory != null) {
            boolean exists = categoriesRepository.findByIdAndUser(idCategory, idUser).isPresent();

            if (!exists) {
                idCategory = null;
            }

        }

        // Create updated entity (preserving original createdAt)
        TasksEntity updatedTask = new TasksEntity(
            idUser,
            taskId,
            existingTask.getCreatedAt(),
            timeStamp,
            tasksCreateUpdateCommand.taskName().toLowerCase().trim(),
            tasksCreateUpdateCommand.description(),
            idCategory,
            null,
            tasksCreateUpdateCommand.color(),
            tasksCreateUpdateCommand.priority(),
            tasksCreateUpdateCommand.startTime(),
            tasksCreateUpdateCommand.endTime(),
            tasksCreateUpdateCommand.location(),
            tasksCreateUpdateCommand.allDay(),
            tasksCreateUpdateCommand.reminderTime(),
            tasksCreateUpdateCommand.notifyActive(),
            tasksCreateUpdateCommand.status(),
            tasksCreateUpdateCommand.dueDate()
        );

        // Publish update event to Kafka
        tasksEventProducer.producerCreateUpdate(updatedTask);

    }

}