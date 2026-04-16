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
public class CategoriesCreateUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final CategoriesRepository categoriesRepository;
    private final CategoriesEventProducer categoriesEventProducer;

    public CategoriesCreateUseCase(

        CategoriesRepository categoriesRepository,
        CategoriesEventProducer categoriesEventProducer

    ) {

        this.categoriesRepository = categoriesRepository;
        this.categoriesEventProducer = categoriesEventProducer;

    }

    // ===================================================== ( constructor end )

    @Transactional
    public String execute(

        Map<String, Object> credentialsData,
        CategoriesCreateUpdateCommand categoriesCreateUpdateCommand

    ) {

        // Credentials
        UUID idUser = UUID.fromString((String) credentialsData.get("id"));

        // Duplicated
        if ( categoriesRepository.existsByIdUserAndCategoryName(
            idUser,
            categoriesCreateUpdateCommand.categoryName().toLowerCase().trim()
        )) {
            throw new DomainException(DomainExceptionEnum.TASKS_DUPLICATED_CATEGORY);
        }

        // Create category id and timestamp
        UUID idCreated = UUID.randomUUID();
        LocalDateTime timeStamp = LocalDateTime.now();

        // Create entity
        CategoriesEntity createNewCategory = new CategoriesEntity(
            idUser,
            idCreated,
            timeStamp,
            timeStamp,
            categoriesCreateUpdateCommand.categoryName().toLowerCase().trim()
        );

        // Create message
        categoriesEventProducer.producerCreateUpdate(createNewCategory);

        // Return created id
        return idCreated.toString();

    }

}