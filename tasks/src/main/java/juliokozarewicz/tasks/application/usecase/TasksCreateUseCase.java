package juliokozarewicz.tasks.application.usecase;

import juliokozarewicz.tasks.application.input.TasksCreateInput;
import juliokozarewicz.tasks.domain.entity.TasksEntity;
import juliokozarewicz.tasks.infrastructure.persistence.model.TasksModel;
import juliokozarewicz.tasks.infrastructure.persistence.repositoryimpl.TasksRepositoryImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TasksCreateUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final TasksRepositoryImpl tasksRepository;

    public TasksCreateUseCase (
        TasksRepositoryImpl tasksRepository
    ) {
        this.tasksRepository = tasksRepository;
    }

    // ===================================================== ( constructor end )

    public void execute(

        TasksCreateInput tasksCreateInput

    ) {

        // Business rules
        TasksEntity taskCreate = new TasksEntity(
            tasksCreateInput.priority(),
            tasksCreateInput.startTime(),
            tasksCreateInput.endTime(),
            tasksCreateInput.allDay(),
            tasksCreateInput.reminderTime(),
            tasksCreateInput.dueDate()
        );

        // JPA to model
        TasksModel newTask = new TasksModel();
        newTask.setId(UUID.randomUUID());
        newTask.setCreatedAt(LocalDateTime.now());
        newTask.setUpdatedAt(LocalDateTime.now());
        newTask.setTaskName(tasksCreateInput.taskName());
        newTask.setDescription(tasksCreateInput.description());
        newTask.setCategory(tasksCreateInput.category());
        newTask.setColor(tasksCreateInput.color());
        newTask.setPriority(tasksCreateInput.priority());
        newTask.setStartTime(tasksCreateInput.startTime());
        newTask.setEndTime(tasksCreateInput.endTime());
        newTask.setLocation(tasksCreateInput.location());
        newTask.setAllDay(tasksCreateInput.allDay());
        newTask.setReminderTime(tasksCreateInput.reminderTime());
        newTask.setNotifyActive(tasksCreateInput.notifyActive());
        newTask.setStatus(tasksCreateInput.status());
        newTask.setDueDate(tasksCreateInput.dueDate());

        tasksRepository.save(newTask);

    }

}