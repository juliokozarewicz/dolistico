package juliokozarewicz.category.infrastructure.persistence.jpa;

import juliokozarewicz.category.infrastructure.persistence.model.CategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CategoryJPA extends JpaRepository<CategoryModel, UUID> {}
