package juliokozarewicz.tasks.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TasksModel {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

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

}