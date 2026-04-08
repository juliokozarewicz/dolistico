package juliokozarewicz.categories.application.usecase;

import juliokozarewicz.categories.application.command.CategoriesCreateUpdateCommand;
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

    private final TasksRepository tasksRepository;
    private final TasksEventProducer tasksEventProducer;

    public CategoriesCreateUseCase(

        TasksRepository tasksRepository,
        TasksEventProducer tasksEventProducer

    ) {

        this.tasksRepository = tasksRepository;
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

        // Create category id and timestamp
        UUID idCreated = UUID.randomUUID();
        LocalDateTime timeStamp = LocalDateTime.now();

        // Create category entity
        // Create category message

        // Return created id
        return idCreated.toString();

    }

}