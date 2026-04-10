package juliokozarewicz.categories.infrastructure.persistence.jpa;

import juliokozarewicz.categories.infrastructure.persistence.model.CategoriesModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriesRepositoryJPA extends
    JpaRepository<CategoriesModel, UUID>, JpaSpecificationExecutor<CategoriesModel> {

    boolean existsByIdUserAndCategoryName(UUID idUser, String categoryName);

    Page<CategoriesModel> findAllByIdUser(
        UUID idUser,
        String categoryName,
        Pageable pageable
    );

    Optional<CategoriesModel> findByIdAndIdUser(UUID id, UUID idUser);

}