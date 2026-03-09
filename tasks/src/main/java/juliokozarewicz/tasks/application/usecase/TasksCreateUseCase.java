package juliokozarewicz.tasks.application.usecase;

import juliokozarewicz.tasks.application.command.TasksCreateCommand;
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
    private final TasksEventProducer tasksEventProducer;

    public TasksCreateUseCase(

        TasksRepository tasksRepository,
        TasksEventProducer tasksEventProducer

    ) {

        this.tasksRepository = tasksRepository;
        this.tasksEventProducer = tasksEventProducer;

    }

    // ===================================================== ( constructor end )

    @Transactional
    public String execute(

        Map<String, Object> credentialsData,
        TasksCreateCommand tasksCreateCommand

    ) {

        // Duplicated task
        if ( tasksRepository.existsByTaskNameAndDueDate(
            tasksCreateCommand.taskName().trim(),
            tasksCreateCommand.dueDate()
        )) {
            throw new DomainException(DomainExceptionEnum.DUPLICATED_TASK);
        }

        // Create task id and time stamp
        UUID idCreated = UUID.randomUUID();
        LocalDateTime timeStamp = LocalDateTime.now();

        // Create entity
        TasksEntity createNewTask = new TasksEntity(
            idCreated,
            timeStamp,
            timeStamp,
            tasksCreateCommand.taskName().trim(),
            tasksCreateCommand.description(),
            tasksCreateCommand.category(),
            tasksCreateCommand.color(),
            tasksCreateCommand.priority(),
            tasksCreateCommand.startTime(),
            tasksCreateCommand.endTime(),
            tasksCreateCommand.location(),
            tasksCreateCommand.allDay(),
            tasksCreateCommand.reminderTime(),
            tasksCreateCommand.notifyActive(),
            tasksCreateCommand.status(),
            tasksCreateCommand.dueDate()
        );

        // Create message
        tasksEventProducer.producer(createNewTask);

        // Return created id
        return idCreated.toString();

    }

}