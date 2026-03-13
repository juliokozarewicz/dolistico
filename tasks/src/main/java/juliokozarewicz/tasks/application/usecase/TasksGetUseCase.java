package juliokozarewicz.tasks.application.usecase;

import juliokozarewicz.tasks.application.command.TasksGetCommand;
import juliokozarewicz.tasks.application.command.TasksGetResponseCommand;
import juliokozarewicz.tasks.domain.entity.TasksEntity;
import juliokozarewicz.tasks.domain.repository.TasksRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    @Transactional(readOnly = true)
    public Map<String, Object> execute (

        Map<String, Object> credentialsData,
        TasksGetCommand tasksGetCommand

    ) {

        // Credentials
        UUID idUser = UUID.fromString((String) credentialsData.get("id"));

        // --------------------------------------------------- (pagination init)
        int pageNumber = tasksGetCommand.pageNumber() != null &&tasksGetCommand.pageNumber() >= 1
        ? tasksGetCommand.pageNumber()
        : 1;

        int pageSize = tasksGetCommand.sizePagination() != null
        ? tasksGetCommand.sizePagination()
        : 10;

        List<TasksEntity> allElements = tasksRepository.findAllByUserId(idUser);

        List<TasksGetResponseCommand> content = allElements.stream()
        .sorted(Comparator.comparing(TasksEntity::getCreatedAt).reversed())
        .skip((long) (pageNumber - 1) * pageSize)
        .limit(pageSize)
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
        // ---------------------------------------------------- (pagination end)

        // Return map
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", content);
        result.put("currentPage", pageNumber);
        result.put("pageSize", pageSize);
        result.put("totalElements", allElements.size());
        result.put("totalPages", (int) Math.ceil((double) allElements.size() / pageSize));
        return result;

    }

}