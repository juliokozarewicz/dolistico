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
public class TasksCreateUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final TasksRepository tasksRepository;
    private final CategoriesRepository categoriesRepository;
    private final TasksEventProducer tasksEventProducer;

    public TasksCreateUseCase(

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
    public String execute(

        Map<String, Object> credentialsData,
        TasksCreateUpdateCommand tasksCreateUpdateCommand

    ) {

        // Credentials
        UUID idUser = UUID.fromString((String) credentialsData.get("id"));
        // String idLevelUser = credentialsData.get("level").toString();

        // Duplicated task
        if ( tasksRepository.existsByIdUserAndTaskNameAndDueDate(
            idUser,
            tasksCreateUpdateCommand.taskName().toLowerCase().trim(),
            tasksCreateUpdateCommand.dueDate()
        )) {
            throw new DomainException(DomainExceptionEnum.TASKS_DUPLICATED_TASK);
        }

        // Create task id and timestamp
        UUID idCreated = UUID.randomUUID();
        LocalDateTime timeStamp = LocalDateTime.now();

        // Verify category
        UUID idCategory = tasksCreateUpdateCommand.category();

        if (idCategory != null) {
            boolean exists = categoriesRepository.findByIdAndUser(idCategory, idUser).isPresent();

            if (!exists) {
                idCategory = null;
            }

        }

        // Create entity
        TasksEntity createNewTask = new TasksEntity(
            idUser,
            idCreated,
            timeStamp,
            timeStamp,
            tasksCreateUpdateCommand.taskName().toLowerCase().trim(),
            tasksCreateUpdateCommand.description(),
            idCategory,
            null,
            tasksCreateUpdateCommand.color().toLowerCase().trim(),
            tasksCreateUpdateCommand.priority(),
            tasksCreateUpdateCommand.startTime(),
            tasksCreateUpdateCommand.endTime(),
            tasksCreateUpdateCommand.location(),
            tasksCreateUpdateCommand.allDay(),
            tasksCreateUpdateCommand.reminderTime(),
            tasksCreateUpdateCommand.notifyActive(),
            tasksCreateUpdateCommand.status().toLowerCase().trim(),
            tasksCreateUpdateCommand.dueDate()
        );

        // Create message
        tasksEventProducer.producerCreateUpdate(createNewTask);

        // Return created id
        return idCreated.toString();

    }

}