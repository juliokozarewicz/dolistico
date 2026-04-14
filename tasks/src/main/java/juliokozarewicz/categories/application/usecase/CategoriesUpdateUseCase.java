package juliokozarewicz.categories.application.usecase;

import juliokozarewicz.categories.application.command.CategoriesCreateUpdateCommand;
import juliokozarewicz.categories.domain.entity.CategoriesEntity;
import juliokozarewicz.categories.domain.repository.CategoriesRepository;
import juliokozarewicz.categories.infrastructure.messaging.producer.CategoriesEventProducer;
import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class CategoriesUpdateUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final CategoriesRepository categoriesRepository;
    private final CategoriesEventProducer categoriesEventProducer;

    public CategoriesUpdateUseCase(

        CategoriesRepository categoriesRepository,
        CategoriesEventProducer categoriesEventProducer

    ) {

        this.categoriesRepository = categoriesRepository;
        this.categoriesEventProducer = categoriesEventProducer;

    }

    // ===================================================== ( constructor end )

    @Transactional
    public void execute(

        Map<String, Object> credentialsData,
        UUID idCategory,
        CategoriesCreateUpdateCommand categoriesCreateUpdateCommand

    ) {

        // Credentials
        UUID idUser = UUID.fromString((String) credentialsData.get("id"));

        // Find existing category
        CategoriesEntity existingCategory = categoriesRepository.findByIdAndUser(idCategory, idUser)
        .orElseThrow(() -> new DomainException(DomainExceptionEnum.CATEGORY_NOT_FOUND));

        // Duplicated check (exclude current id)
        if ( categoriesRepository.existsByCategoryNameAndAndIdNot(
                idUser,
                categoriesCreateUpdateCommand.categoryName().toLowerCase().trim(),
                idCategory
        )) {
            throw new DomainException(DomainExceptionEnum.DUPLICATED_CATEGORY);
        }

        // New timestamp for update
        LocalDateTime timeStamp = LocalDateTime.now();

        // Create entity
        CategoriesEntity createNewCategory = new CategoriesEntity(
            idUser,
            idCategory,
            existingCategory.getCreatedAt(),
            timeStamp,
            categoriesCreateUpdateCommand.categoryName().toLowerCase().trim()
        );

        // Create message
        categoriesEventProducer.producerCreateUpdate(createNewCategory);

    }

}