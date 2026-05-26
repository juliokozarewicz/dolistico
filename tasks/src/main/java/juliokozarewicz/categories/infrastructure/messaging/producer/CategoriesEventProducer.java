package juliokozarewicz.categories.infrastructure.messaging.producer;

import juliokozarewicz.categories.domain.entity.CategoriesEntity;
import juliokozarewicz.categories.infrastructure.messaging.enums.CategoriesMessagingTopicEnum;
import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Service
public class CategoriesEventProducer {

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    // ==================================================== ( constructor init )
    private static final Logger logger = LoggerFactory.getLogger(CategoriesEventProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public CategoriesEventProducer(

        KafkaTemplate<String, byte[]> kafkaTemplate,
        ObjectMapper objectMapper

    ) {

        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;

    }
    // ===================================================== ( constructor end )

    // create update producer
    public void producerCreateUpdate (

        CategoriesEntity message

    ) {

        try {

            // Convert object to JSON bytes
            byte[] payload = objectMapper.writeValueAsBytes(message);

            // Send as raw bytes
            kafkaTemplate.send(
                CategoriesMessagingTopicEnum.CATEGORIES_CREATE_UPDATE_PERSIST,
                payload
            ).get(5, TimeUnit.SECONDS);

        } catch (Exception e) {

            // Logs
            logger.atError()
            .addKeyValue("topic", CategoriesMessagingTopicEnum.CATEGORIES_CREATE_UPDATE_PERSIST)
            .log("Error producing message: [ CategoriesEventProducer.producerCreateUpdate() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

    // delete producer
    public void producerDelete (

        CategoriesEntity message

    ) {

        try {

            // Convert object to JSON bytes
            byte[] payload = objectMapper.writeValueAsBytes(message);

            // Send as raw bytes
            kafkaTemplate.send(
                CategoriesMessagingTopicEnum.CATEGORIES_DELETE_PERSIST,
                payload
            ).get(5, TimeUnit.SECONDS);

        } catch (Exception e) {

            // Logs
            logger.atError()
            .addKeyValue("topic", CategoriesMessagingTopicEnum.CATEGORIES_DELETE_PERSIST)
            .log("Error producing message: [ CategoriesEventProducer.producerDelete() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}