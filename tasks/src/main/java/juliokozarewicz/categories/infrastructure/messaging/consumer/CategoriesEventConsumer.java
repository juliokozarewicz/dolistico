package juliokozarewicz.categories.infrastructure.messaging.consumer;

import juliokozarewicz.categories.domain.entity.CategoriesEntity;
import juliokozarewicz.categories.domain.repository.CategoriesRepository;
import juliokozarewicz.categories.infrastructure.messaging.enums.MessagingTopicEnum;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class CategoriesEventConsumer {

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    // ==================================================== ( constructor init )
    private final CategoriesRepository categoriesRepository;
    private final ObjectMapper objectMapper;

    public CategoriesEventConsumer(

        CategoriesRepository categoriesRepository,
        ObjectMapper objectMapper

    ) {

        this.categoriesRepository = categoriesRepository;
        this.objectMapper = objectMapper;

    }
    // ===================================================== ( constructor end )

     // create update consumer
    @KafkaListener(
        topics = MessagingTopicEnum.CATEGORIES_CREATE_UPDATE_PERSIST,
        groupId = MessagingTopicEnum.CATEGORIES_CREATE_UPDATE_PERSIST
    )
    public void consumerCreateUpdate (

        ConsumerRecord<String, byte[]> record,
        Acknowledgment ack

    ) {

        try {

            // Save entity in DB
            byte[] payload = record.value();
            CategoriesEntity categoriesEntity = objectMapper.readValue(payload, CategoriesEntity.class);
            categoriesRepository.save(categoriesEntity);
            ack.acknowledge();

        } catch (Exception e) {

            throw new InternalError(
                "Error while consuming the Kafka message " +
                "[ CategoriesEventConsumer.consumerCreateUpdate() ]: " + e
            );

        }

    }

    // delete consumer
    @KafkaListener(
        topics = MessagingTopicEnum.CATEGORIES_DELETE_PERSIST,
        groupId = MessagingTopicEnum.CATEGORIES_DELETE_PERSIST
    )
    public void consumerDelete (

        ConsumerRecord<String, byte[]> record,
        Acknowledgment ack

    ) {

        try {

            // Save entity in DB
            byte[] payload = record.value();
            CategoriesEntity categoriesEntity = objectMapper.readValue(payload, CategoriesEntity.class);
            categoriesRepository.delete(categoriesEntity);
            ack.acknowledge();

        } catch (Exception e) {

            throw new InternalError(
                "Error while consuming the Kafka message " +
                "[ CategoriesEventConsumer.consumerDelete() ]: " + e
            );

        }

    }

}