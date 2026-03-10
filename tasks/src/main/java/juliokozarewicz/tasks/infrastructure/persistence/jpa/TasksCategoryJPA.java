package juliokozarewicz.tasks.infrastructure.persistence.jpa;

import juliokozarewicz.tasks.infrastructure.persistence.model.CategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TasksCategoryJPA extends JpaRepository<CategoryModel, UUID> {}
