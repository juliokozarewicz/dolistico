package juliokozarewicz.tasks.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class TasksModel {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "task_name", nullable = false, length = 255)
    private String taskName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "category", length = 255)
    private String category;

    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "priority", nullable = false)
    private int priority;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "is_all_day", nullable = false)
    private boolean allDay;

    @Column(name = "reminder_time")
    private LocalDateTime reminderTime;

    @Column(name = "notify_active", nullable = false)
    private boolean notifyActive;

    @Column(name = "status", nullable = false, length = 255)
    private String status;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    public static TasksModel create(

        UUID id,
        String taskName,
        String description,
        String category,
        String color,
        int priority,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String location,
        boolean allDay,
        LocalDateTime reminderTime,
        boolean notifyActive,
        String status,
        LocalDateTime dueDate

    ) {

        TasksModel entity = new TasksModel();
        entity.id = id;
        entity.timestamp = LocalDateTime.now();
        entity.updatedAt = LocalDateTime.now();
        entity.taskName = taskName;
        entity.description = description;
        entity.category = category;
        entity.color = color;
        entity.priority = priority;
        entity.startTime = startTime;
        entity.endTime = endTime;
        entity.location = location;
        entity.allDay = allDay;
        entity.reminderTime = reminderTime;
        entity.notifyActive = notifyActive;
        entity.status = status;
        entity.dueDate = dueDate;

        return entity;

    }

    public void update(

        String taskName,
        String description,
        String category,
        String color,
        int priority,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String location,
        boolean allDay,
        LocalDateTime reminderTime,
        boolean notifyActive,
        String status,
        LocalDateTime dueDate

    ) {

        this.taskName = taskName;
        this.description = description;
        this.category = category;
        this.color = color;
        this.priority = priority;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.allDay = allDay;
        this.reminderTime = reminderTime;
        this.notifyActive = notifyActive;
        this.status = status;
        this.dueDate = dueDate;
        this.updatedAt = LocalDateTime.now();

    }

}