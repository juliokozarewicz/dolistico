package juliokozarewicz.tasks.infrastructure.persistence.impl;

import juliokozarewicz.tasks.domain.entity.TasksEntity;
import juliokozarewicz.tasks.domain.repository.TasksRepository;
import juliokozarewicz.tasks.infrastructure.persistence.jpa.TasksCategoryJPA;
import juliokozarewicz.tasks.infrastructure.persistence.jpa.TasksRepositoryJPA;
import juliokozarewicz.tasks.infrastructure.persistence.model.CategoryModel;
import juliokozarewicz.tasks.infrastructure.persistence.model.TasksModel;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class TasksRepositoryImpl implements TasksRepository {

    private final TasksRepositoryJPA tasksRepositoryJPA;
    private final TasksCategoryJPA tasksCategoryJPA;

    public TasksRepositoryImpl(

        TasksRepositoryJPA jpaRepository,
        TasksCategoryJPA tasksCategoryJPA

    ) {

        this.tasksRepositoryJPA = jpaRepository;
        this.tasksCategoryJPA = tasksCategoryJPA;

    }

    @Override
    public boolean existsByTaskNameAndDueDate(String taskName, LocalDateTime dueDate) {
        return tasksRepositoryJPA.existsByTaskNameAndDueDate(taskName, dueDate);
    }

    @Override
    public void save(

        TasksEntity tasksEntity

    ) {

        // --------------------------------------------------- ( Category init )
        CategoryModel categoryModel = null;

        if (tasksEntity.getCategory() != null) {
            categoryModel = tasksCategoryJPA.findById(tasksEntity.getCategory())
            .orElse(null);
        }
        // ---------------------------------------------------- ( Category end )

        // Mapper
        TasksModel model = TasksModel.builder()
            .idUser(tasksEntity.getIdUser())
            .id(tasksEntity.getIdCreated())
            .createdAt(tasksEntity.getCreatedAt())
            .updatedAt(tasksEntity.getUpdatedAt())
            .taskName(tasksEntity.getTaskName())
            .description(tasksEntity.getDescription())
            .category(categoryModel)
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

    @Override
    public List<TasksEntity> findAllByUserId(UUID idUser) {

        return tasksRepositoryJPA.findAllByIdUser(idUser)
                .stream()
                .map(model -> new TasksEntity(
                        model.getIdUser(),
                        model.getId(),
                        model.getCreatedAt(),
                        model.getUpdatedAt(),
                        model.getTaskName(),
                        model.getDescription(),
                        model.getCategory() != null ? model.getCategory().getId() : null,
                        model.getCategory() != null ? model.getCategory().getCategoryName() : null,
                        model.getColor(),
                        model.getPriority(),
                        model.getStartTime(),
                        model.getEndTime(),
                        model.getLocation(),
                        model.isAllDay(),
                        model.getReminderTime(),
                        model.isNotifyActive(),
                        model.getStatus(),
                        model.getDueDate()
                ))
                .collect(Collectors.toList());
    }

}