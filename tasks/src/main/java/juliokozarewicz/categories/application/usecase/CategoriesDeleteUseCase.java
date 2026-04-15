package juliokozarewicz.categories.application.usecase;

import juliokozarewicz.categories.domain.repository.CategoriesRepository;
import juliokozarewicz.categories.infrastructure.messaging.producer.CategoriesEventProducer;
import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.UUID;

@Service
public class CategoriesDeleteUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final CategoriesRepository categoriesRepository;
    private final CategoriesEventProducer categoriesEventProducer;

    public CategoriesDeleteUseCase(

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
        String idTask

    ) {

        // Credentials
        UUID idUser = UUID.fromString((String) credentialsData.get("id"));
        UUID idDelete = UUID.fromString(idTask);

        // Find existing (ensures ownership)
        var categoryEntity = categoriesRepository.findByIdAndUser(idDelete, idUser)
        .orElseThrow(() -> new DomainException(DomainExceptionEnum.CATEGORY_NOT_FOUND));

        // Publish delete event to Kafka
        categoriesEventProducer.producerDelete(categoryEntity);

    }

}