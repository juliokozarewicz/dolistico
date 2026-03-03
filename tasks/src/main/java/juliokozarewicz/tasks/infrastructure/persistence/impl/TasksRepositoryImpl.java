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
    public boolean existsByTaskNameAndDueDate(String taskName, java.time.LocalDate dueDate) {
        return tasksRepositoryJPA.existsByTaskNameAndDueDate(taskName, dueDate);
    }

    @Override
    public void save(TasksEntity tasksEntity) {

        // Mapper
        TasksModel createNewTask = new TasksModel();
        createNewTask.setId(tasksEntity.getId() != null ? tasksEntity.getId() : UUID.randomUUID());
        createNewTask.setTaskName(tasksEntity.getTaskName());
        createNewTask.setDescription(tasksEntity.getDescription());
        createNewTask.setCategory(tasksEntity.getCategory());
        createNewTask.setColor(tasksEntity.getColor());
        createNewTask.setPriority(tasksEntity.getPriority());
        createNewTask.setStartTime(tasksEntity.getStartTime());
        createNewTask.setEndTime(tasksEntity.getEndTime());
        createNewTask.setLocation(tasksEntity.getLocation());
        createNewTask.setAllDay(tasksEntity.isAllDay());
        createNewTask.setReminderTime(tasksEntity.getReminderTime());
        createNewTask.setNotifyActive(tasksEntity.isNotifyActive());
        createNewTask.setStatus(tasksEntity.getStatus());
        createNewTask.setDueDate(tasksEntity.getDueDate());
        createNewTask.setCreatedAt(tasksEntity.getCreatedAt() != null ? tasksEntity.getCreatedAt() : LocalDateTime.now());
        createNewTask.setUpdatedAt(LocalDateTime.now());

        // Commit
        TasksModel saved = tasksRepositoryJPA.save(createNewTask);

    }

}