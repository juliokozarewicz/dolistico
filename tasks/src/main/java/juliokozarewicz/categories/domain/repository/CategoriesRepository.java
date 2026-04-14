package juliokozarewicz.categories.domain.repository;

import juliokozarewicz.categories.domain.entity.CategoriesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface CategoriesRepository {

    boolean existsByIdUserAndCategoryName(UUID idUser, String categoryName);

    Page<CategoriesEntity> findAllByIdUser(
        UUID idUser,
        String categoryName,
        Pageable pageable
    );

    void save(CategoriesEntity category);

    Optional<CategoriesEntity> findByIdAndUser(UUID categoryId, UUID idUser);

    boolean existsByCategoryNameAndAndIdNot (
        UUID idUser,
        String categoryName,
        UUID excludeId
    );


    void delete(CategoriesEntity category);

}