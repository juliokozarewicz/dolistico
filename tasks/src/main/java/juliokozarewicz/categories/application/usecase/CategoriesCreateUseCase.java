package juliokozarewicz.categories.application.usecase;

import juliokozarewicz.categories.application.command.CategoriesCreateUpdateCommand;
import juliokozarewicz.categories.domain.repository.CategoriesRepository;
import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;
import juliokozarewicz.tasks.domain.repository.TasksRepository;
import juliokozarewicz.tasks.infrastructure.messaging.producer.TasksEventProducer;
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
    private final TasksEventProducer tasksEventProducer;

    public CategoriesCreateUseCase(

        CategoriesRepository categoriesRepository,
        TasksEventProducer tasksEventProducer

    ) {

        this.categoriesRepository = categoriesRepository;
        this.tasksEventProducer = tasksEventProducer;

    }

    // ===================================================== ( constructor end )

    @Transactional
    public String execute(

        Map<String, Object> credentialsData,
        CategoriesCreateUpdateCommand categoriesCreateUpdateCommand

    ) {

        // Credentials
        UUID idUser = UUID.fromString((String) credentialsData.get("id"));

        // Duplicated category
        if ( categoriesRepository.existsByIdUserAndCategoryName(
            idUser,
            categoriesCreateUpdateCommand.categoryName().trim()
        )) {
            throw new DomainException(DomainExceptionEnum.DUPLICATED_CATEGORY);
        }

        // Create category id and timestamp
        UUID idCreated = UUID.randomUUID();
        LocalDateTime timeStamp = LocalDateTime.now();

        // Create category entity
        // Create category message

        // Return created id
        return idCreated.toString();

    }

}