package juliokozarewicz.categories.infrastructure.persistence.jpa;

import juliokozarewicz.categories.infrastructure.persistence.model.CategoriesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CategoriesJPA extends JpaRepository<CategoriesModel, UUID> {}
