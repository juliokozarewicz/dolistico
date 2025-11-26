package juliokozarewicz.tasks.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "juliokozarewicz/tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TasksEntity {

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
    private boolean allDay = false;

    @Column(name = "reminder_time")
    private LocalDateTime reminderTime;

    @Column(name = "notify", nullable = false)
    private boolean notify = false;

    @Column(name = "status", nullable = false, length = 255)
    private String status;

    @Column(name = "due_date")
    private LocalDate dueDate;

}
