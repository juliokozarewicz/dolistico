package juliokozarewicz.categories.infrastructure.persistence.impl;

import juliokozarewicz.categories.domain.entity.CategoriesEntity;
import juliokozarewicz.categories.domain.repository.CategoriesRepository;
import juliokozarewicz.categories.infrastructure.persistence.jpa.CategoriesRepositoryJPA;
import juliokozarewicz.categories.infrastructure.persistence.model.CategoriesModel;
import juliokozarewicz.categories.infrastructure.persistence.specification.CategoriesGetSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CategoriesRepositoryImpl implements CategoriesRepository {

    private final CategoriesRepositoryJPA categoriesRepositoryJPA;

    public CategoriesRepositoryImpl(CategoriesRepositoryJPA categoriesRepositoryJPA) {
        this.categoriesRepositoryJPA = categoriesRepositoryJPA;
    }

    // ======================================================== ( mappers init )

    // ENTITY -> MODEL
    private CategoriesModel toModel(CategoriesEntity entity) {
        return CategoriesModel.builder()
            .id(entity.getIdCreated())
            .idUser(entity.getIdUser())
            .categoryName(entity.getCategoryName())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    // MODEL -> ENTITY
    private CategoriesEntity toEntity(CategoriesModel model) {
        return new CategoriesEntity(
            model.getIdUser(),
            model.getId(),
            model.getCreatedAt(),
            model.getUpdatedAt(),
            model.getCategoryName()
        );
    }

    // ========================================================= ( mappers end )

    @Override
    public boolean existsByIdUserAndCategoryName(UUID idUser, String categoryName) {
        return categoriesRepositoryJPA.existsByIdUserAndCategoryName(idUser, categoryName);
    }

    @Override
    public Page<CategoriesEntity> findAllByIdUser(

        UUID idUser,
        String categoryName,
        Pageable pageable

    ) {

        var spec = CategoriesGetSpecification.filter(
            categoryName,
            idUser
        );

        return categoriesRepositoryJPA
        .findAll(spec, pageable)
        .map(this::toEntity);
    }

    @Override
    public void save(CategoriesEntity category) {
        categoriesRepositoryJPA.save(toModel(category));
    }

    @Override
    public Optional<CategoriesEntity> findByIdAndUser(UUID categoryId, UUID idUser) {
        return categoriesRepositoryJPA.findByIdAndIdUser(categoryId, idUser)
        .map(this::toEntity);
    }

    @Override
    public boolean existsByCategoryNameAndAndIdNot(
        UUID idUser,
        String categoryName,
        UUID excludeId
    ) {
        return categoriesRepositoryJPA.existsByIdUserAndCategoryNameAndIdNot(
            idUser,
            categoryName,
            excludeId
        );
    }

    @Override
    public void delete(CategoriesEntity category) {
        categoriesRepositoryJPA.deleteById(category.getIdCreated());
    }

}