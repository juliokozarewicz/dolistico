package juliokozarewicz.tasks.application.usecase;

import juliokozarewicz.tasks.application.input.TasksCreateInput;
import juliokozarewicz.tasks.application.mapper.TasksMapper;
import juliokozarewicz.tasks.domain.entity.TasksEntity;
import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;
import juliokozarewicz.tasks.domain.repository.TasksRepository;
import juliokozarewicz.tasks.infrastructure.messaging.producer.TasksEventProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public String execute( TasksCreateInput tasksCreateInput ) {

        // Duplicated task
        if ( tasksRepository.existsByTaskNameAndDueDate(
            tasksCreateInput.taskName(),
            tasksCreateInput.dueDate()
        )) {
            throw new DomainException(DomainExceptionEnum.DUPLICATED_TASK);
        }

        // Create task id and time stamp
        UUID idCreated = UUID.randomUUID();
        LocalDateTime timeStamp = LocalDateTime.now();

        // Create entity
        TasksEntity createNewTask = TasksMapper.toEntity(
            idCreated,
            timeStamp,
            timeStamp,
            tasksCreateInput
        );

        // Create message
        tasksEventProducer.publish(
            TasksMapper.toDto(createNewTask)
        );

        // Return created id
        return idCreated.toString();

    }

}