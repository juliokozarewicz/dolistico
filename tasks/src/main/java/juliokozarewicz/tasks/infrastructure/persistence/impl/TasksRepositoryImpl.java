package juliokozarewicz.tasks.infrastructure.persistence.impl;

import juliokozarewicz.tasks.domain.entity.TasksEntity;
import juliokozarewicz.tasks.domain.repository.TasksRepository;
import juliokozarewicz.tasks.infrastructure.persistence.jpa.TasksRepositoryJPA;
import juliokozarewicz.tasks.infrastructure.persistence.model.TasksModel;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class TasksRepositoryImpl implements TasksRepository {

    private final TasksRepositoryJPA tasksRepositoryJPA;

    public TasksRepositoryImpl(TasksRepositoryJPA jpaRepository) {
        this.tasksRepositoryJPA = jpaRepository;
    }

    @Override
    public boolean existsByTaskNameAndDueDate(String taskName, LocalDateTime dueDate) {
        return tasksRepositoryJPA.existsByTaskNameAndDueDate(taskName, dueDate);
    }

    @Override
    public void save(TasksEntity tasksEntity) {

        // Mapper
        TasksModel model = TasksModel.builder()
            .id(tasksEntity.getId())
            .createdAt(tasksEntity.getCreatedAt())
            .updatedAt(tasksEntity.getUpdatedAt())
            .taskName(tasksEntity.getTaskName())
            .description(tasksEntity.getDescription())
            .category(tasksEntity.getCategory())
            .color(tasksEntity.getColor())
            .priority(tasksEntity.getPriority())
            .startTime(tasksEntity.getStartTime())
            .endTime(tasksEntity.getEndTime())
            .location(tasksEntity.getLocation())
            .allDay(tasksEntity.isAllDay())
            .reminderTime(tasksEntity.getReminderTime())
            .notifyActive(tasksEntity.isNotifyActive())
            .status(tasksEntity.getStatus())
            .dueDate(tasksEntity.getDueDate())
            .build();

        // Save
        tasksRepositoryJPA.save(model);

    }

}