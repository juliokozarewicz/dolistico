package juliokozarewicz.tasks.application.usecase;

import juliokozarewicz.tasks.application.command.TasksGetCommand;
import juliokozarewicz.tasks.application.command.TasksGetResponseCommand;
import juliokozarewicz.tasks.domain.entity.TasksEntity;
import juliokozarewicz.tasks.domain.repository.TasksRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TasksGetUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final TasksRepository tasksRepository;

    public TasksGetUseCase (

        TasksRepository tasksRepository

    ) {

        this.tasksRepository = tasksRepository;

    }

    // ===================================================== ( constructor end )

    @Transactional
    public List<TasksGetResponseCommand> execute (

        Map<String, Object> credentialsData,
        TasksGetCommand tasksGetCommand

    ) {

        // Credentials
        UUID idUser = UUID.fromString((String) credentialsData.get("id"));

        // Get tasks
         List<TasksEntity> dataReturnedRaw = tasksRepository.findAllByUserId(idUser);

         // Map to response command
        List<TasksGetResponseCommand> dataResponse = dataReturnedRaw.stream()
            .map(entity -> new TasksGetResponseCommand(
                entity.getIdCreated(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getTaskName(),
                entity.getDescription(),
                entity.getCategory() != null
                    ? new TasksGetResponseCommand.CategoryCommand(
                        entity.getCategory(),
                        entity.getCategoryName()
                    )
                    : null,
                entity.getColor(),
                entity.getPriority(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getLocation(),
                entity.isAllDay(),
                entity.getReminderTime(),
                entity.isNotifyActive(),
                entity.getStatus(),
                entity.getDueDate()
            ))
            .collect(Collectors.toList());

        // Return all tasks
        return dataResponse;

    }

}