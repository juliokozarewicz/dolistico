package juliokozarewicz.tasks.application.usecase;

import juliokozarewicz.tasks.application.input.TasksCreateInput;
import juliokozarewicz.tasks.domain.entity.TasksEntity;
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
    public TasksEntity execute( TasksCreateInput tasksCreateInput ) {

        TasksEntity createNewTask = new TasksEntity(
            UUID.randomUUID(),
            LocalDateTime.now(),
            LocalDateTime.now(),
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

        return tasksRepository.save(createNewTask);

    }

}