package juliokozarewicz.categories.application.usecase;

import juliokozarewicz.categories.application.command.CategoriesGetResponseCommand;

import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;
import juliokozarewicz.categories.domain.repository.CategoriesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class CategoriesGetByIdUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final CategoriesRepository categoriesRepository;

    public CategoriesGetByIdUseCase(

        CategoriesRepository categoriesRepository

    ) {

        this.categoriesRepository = categoriesRepository;

    }

    // ===================================================== ( constructor end )

    @Transactional(readOnly = true)
    public CategoriesGetResponseCommand execute (

        UUID idUser,
        String idTask

    ) {

        // Category id
        UUID idCategory = UUID.fromString(idTask);

        // Get Category by id
        CategoriesGetResponseCommand categoriesByID = categoriesRepository.findByIdAndUser(idCategory, idUser)
        .map(categories -> new CategoriesGetResponseCommand(
            categories.getIdCreated(),
            categories.getCreatedAt(),
            categories.getUpdatedAt(),
            categories.getCategoryName()
        ))
        .orElseThrow(() -> new DomainException(DomainExceptionEnum.TASKS_CATEGORY_NOT_FOUND));

        // Category not found
        if  (categoriesByID == null) {
            throw new DomainException(DomainExceptionEnum.TASKS_CATEGORY_NOT_FOUND);
        }

        // Return category by id
        return categoriesByID;

    }

}