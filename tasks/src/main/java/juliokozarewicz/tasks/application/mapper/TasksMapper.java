package juliokozarewicz.tasks.application.mapper;

import juliokozarewicz.tasks.application.input.TasksCreateInput;
import juliokozarewicz.tasks.application.output.TasksCreateMessageOutput;
import juliokozarewicz.tasks.domain.entity.TasksEntity;
import java.time.LocalDateTime;
import java.util.UUID;

public class TasksMapper {

    public static TasksEntity toEntity(

        UUID idCreated,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        TasksCreateInput input

    ) {

        return new TasksEntity(

            idCreated,
            createdAt,
            updatedAt,
            input.taskName(),
            input.description(),
            input.category(),
            input.color(),
            input.priority(),
            input.startTime(),
            input.endTime(),
            input.location(),
            input.allDay(),
            input.reminderTime(),
            input.notifyActive(),
            input.status(),
            input.dueDate()

        );

    }

    public static TasksCreateMessageOutput toDto(TasksEntity entity) {

        return new TasksCreateMessageOutput(
            entity.getIdCreated(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getTaskName(),
            entity.getDescription(),
            entity.getCategory(),
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
        );

    }

}