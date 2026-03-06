package juliokozarewicz.tasks.application.mapper;

import juliokozarewicz.tasks.application.dto.TasksCreateInputAppDTO;
import juliokozarewicz.tasks.application.dto.TasksCreateMessageAppDTO;
import juliokozarewicz.tasks.domain.entity.TasksEntity;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface TasksMapper {

     static TasksEntity toEntity(
        UUID idCreated,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        TasksCreateInputAppDTO input
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

    TasksCreateMessageAppDTO toDto(TasksEntity entity);

}