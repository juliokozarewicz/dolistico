package juliokozarewicz.categories.infrastructure.messaging.producer;

import juliokozarewicz.categories.domain.entity.CategoriesEntity;
import juliokozarewicz.categories.infrastructure.messaging.enums.MessagingTopicEnum;
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
                MessagingTopicEnum.CATEGORIES_CREATE_UPDATE_PERSIST,
                payload
            ).get(10, TimeUnit.SECONDS);

        } catch (Exception e) {

            throw new InternalError(
                "Error while producing the Kafka message " +
                "[ CategoriesEventProducer.producerCreateUpdate() ]: " + e
            );

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
                MessagingTopicEnum.CATEGORIES_DELETE_PERSIST,
                payload
            ).get(10, TimeUnit.SECONDS);

        } catch (Exception e) {

            throw new InternalError(
                "Error while producing the Kafka message " +
                "[ CategoriesEventProducer.producerDelete() ]: " + e
            );

        }

    }

}