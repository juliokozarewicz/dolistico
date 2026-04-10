package juliokozarewicz.tasks.application.usecase;

import juliokozarewicz.tasks.application.command.TasksGetCommand;
import juliokozarewicz.tasks.application.command.TasksGetResponseCommand;
import juliokozarewicz.tasks.domain.entity.TasksEntity;
import juliokozarewicz.tasks.domain.repository.TasksRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

        Pageable pageable = PageRequest.of(
            pageNumber - 1,
            pageSize,
            Sort.by("createdAt").descending()
        );

        Page<TasksEntity> page = tasksRepository.findAllByIdUser(
            idUser,
            tasksGetCommand.taskName(),
            tasksGetCommand.category(),
            tasksGetCommand.priority(),
            tasksGetCommand.location(),
            tasksGetCommand.status(),
            tasksGetCommand.dueDateInit(),
            tasksGetCommand.dueDateEnd(),
            pageable
        );

        List<TasksGetResponseCommand> content = page.getContent()
            .stream()
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
            .toList();
        // ---------------------------------------------------- (pagination end)

        // Return map
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", content);
        result.put("currentPage", pageNumber);
        result.put("pageSize", pageSize);
        result.put("totalPages", page.getTotalPages());
        result.put("totalElements", page.getTotalElements());
        return result;

    }

}