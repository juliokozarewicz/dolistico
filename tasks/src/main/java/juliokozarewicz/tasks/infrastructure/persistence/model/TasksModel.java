package juliokozarewicz.tasks.infrastructure.persistence.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tasks", schema = "tasks")
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

    // Owner
    @Column(name = "id_user", updatable = false, nullable = false)
    private UUID idUser;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_task_category"))
    private CategoryModel category;

}