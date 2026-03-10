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
@Table(name = "category", schema = "tasks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CategoryModel {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    // Owner
    @Column(name = "id_user", updatable = false, nullable = false)
    private UUID idUser;

    // Relations
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<TasksModel> tasks;

}