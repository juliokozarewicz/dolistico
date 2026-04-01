package juliokozarewicz.tasks.infrastructure.persistence.impl;

import juliokozarewicz.tasks.domain.entity.TasksEntity;
import juliokozarewicz.tasks.domain.repository.TasksRepository;
import juliokozarewicz.tasks.infrastructure.persistence.jpa.TasksCategoryJPA;
import juliokozarewicz.tasks.infrastructure.persistence.jpa.TasksRepositoryJPA;
import juliokozarewicz.tasks.infrastructure.persistence.model.CategoryModel;
import juliokozarewicz.tasks.infrastructure.persistence.model.TasksModel;
import juliokozarewicz.tasks.infrastructure.persistence.specification.TasksGetUserSpecification;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    // =============================================== ( category helpers init )

    private CategoryModel getCategory(UUID categoryId) {
        return categoryId == null
            ? null
            : tasksCategoryJPA.findById(categoryId).orElse(null);
    }

    private String getCategoryName(TasksModel model) {
        return model.getCategory() != null
            ? model.getCategory().getCategoryName()
            : null;
    }
    // ================================================ ( category helpers end )

    // ======================================================== ( mappers init )

    // ENTITY -> MODEL
    private TasksModel toModel(TasksEntity entity) {
        return TasksModel.builder()
            .idUser(entity.getIdUser())
            .id(entity.getIdCreated())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .taskName(entity.getTaskName())
            .description(entity.getDescription())
            .category(getCategory(entity.getCategory()))
            .color(entity.getColor())
            .priority(entity.getPriority())
            .startTime(entity.getStartTime())
            .endTime(entity.getEndTime())
            .location(entity.getLocation())
            .allDay(entity.isAllDay())
            .reminderTime(entity.getReminderTime())
            .notifyActive(entity.isNotifyActive())
            .status(entity.getStatus())
            .dueDate(entity.getDueDate())
            .build();
    }

    // MODEL -> ENTITY
    private TasksEntity toEntity(TasksModel model) {
        return new TasksEntity(
            model.getIdUser(),
            model.getId(),
            model.getCreatedAt(),
            model.getUpdatedAt(),
            model.getTaskName(),
            model.getDescription(),
            model.getCategory() != null ? model.getCategory().getId() : null,
            getCategoryName(model),
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
        );
    }

    // ========================================================= ( mappers end )

    @Override
    public void save(TasksEntity tasksEntity) {
        tasksRepositoryJPA.save(toModel(tasksEntity));
    }

    // Find All
    @Override
    public List<TasksEntity> findAllByIdUser(
        UUID idUser,
        String taskName,
        String category,
        Integer priority,
        String location,
        String status,
        LocalDate dueDateInit,
        LocalDate dueDateEnd
    ) {

        var spec = TasksGetUserSpecification.filter(
            taskName,
            category,
            priority,
            location,
            status,
            dueDateInit,
            dueDateEnd,
            idUser
        );

        return tasksRepositoryJPA.findAll(spec)
            .stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
    }

    // Find by id
    @Override
    public Optional<TasksEntity> findById(UUID id) {
        return tasksRepositoryJPA.findById(id)
            .map(this::toEntity);
    }

}