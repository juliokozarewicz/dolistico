package juliokozarewicz.categories.infrastructure.persistence.model;

import jakarta.persistence.*;
import juliokozarewicz.tasks.infrastructure.persistence.model.TasksModel;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class CategoriesModel {

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