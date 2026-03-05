package juliokozarewicz.tasks.application.usecase;

import juliokozarewicz.tasks.application.input.TasksCreateInput;
import juliokozarewicz.tasks.domain.entity.TasksEntity;
import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;
import juliokozarewicz.tasks.domain.repository.TasksRepository;
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

    public TasksCreateUseCase(

        TasksRepository tasksRepository

    ) {

        this.tasksRepository = tasksRepository;

    }

    // ===================================================== ( constructor end )

    @Transactional
    public String execute( TasksCreateInput tasksCreateInput ) {

        // Create task id and time stamp
        UUID idCreated = UUID.randomUUID();
        LocalDateTime timeStamp = LocalDateTime.now();

        // Duplicated task
        if ( tasksRepository.existsByTaskNameAndDueDate(
            tasksCreateInput.taskName(),
            tasksCreateInput.dueDate()
        )) {
            throw new DomainException(DomainExceptionEnum.DUPLICATED_TASK);
        }

        // Create entity
        TasksEntity createNewTask = new TasksEntity(
            idCreated,
            timeStamp,
            timeStamp,
            tasksCreateInput.taskName(),
            tasksCreateInput.description(),
            tasksCreateInput.category(),
            tasksCreateInput.color(),
            tasksCreateInput.priority(),
            tasksCreateInput.startTime(),
            tasksCreateInput.endTime(),
            tasksCreateInput.location(),
            tasksCreateInput.allDay(),
            tasksCreateInput.reminderTime(),
            tasksCreateInput.notifyActive(),
            tasksCreateInput.status(),
            tasksCreateInput.dueDate()
        );

        // Save in DB
        tasksRepository.save(createNewTask);

        // Return created id
        return idCreated.toString();

    }

}