package juliokozarewicz.tasks.application.usecase;

import juliokozarewicz.tasks.application.command.TasksGetCommand;
import juliokozarewicz.tasks.application.command.TasksGetResponseCommand;
import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;
import juliokozarewicz.tasks.domain.repository.TasksRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class TasksGetByIdUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final TasksRepository tasksRepository;

    public TasksGetByIdUseCase (

        TasksRepository tasksRepository

    ) {

        this.tasksRepository = tasksRepository;

    }

    // ===================================================== ( constructor end )

    @Transactional(readOnly = true)
    public TasksGetResponseCommand execute (

        UUID idUser,
        String id

    ) {

        // Task id
        UUID idTask = UUID.fromString(id);

        // Get task by id
        TasksGetResponseCommand taskByID = tasksRepository.findByIdAndUser( idTask, idUser )
        .map(task -> new TasksGetResponseCommand(
                task.getIdCreated(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getTaskName(),
                task.getDescription(),
                task.getCategory() != null
                    ? new TasksGetResponseCommand.CategoryCommand(
                        task.getCategory(),
                        task.getCategoryName()
                    )
                    : null,
                task.getColor(),
                task.getPriority(),
                task.getStartTime(),
                task.getEndTime(),
                task.getLocation(),
                task.isAllDay(),
                task.getReminderTime(),
                task.isNotifyActive(),
                task.getStatus(),
                task.getDueDate()
        ))
        .orElse(null);

        // Task not found
        if  (taskByID == null) {
            throw new DomainException(DomainExceptionEnum.TASKS_NOT_FOUND);
        }

        // Return task by id
        return taskByID;

    }

}