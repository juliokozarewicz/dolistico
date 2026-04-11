package juliokozarewicz.tasks.infrastructure.messaging.producer;


import juliokozarewicz.tasks.domain.entity.TasksEntity;
import juliokozarewicz.tasks.infrastructure.messaging.enums.TasksMessagingTopicEnum;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Service
public class TasksEventProducer {

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    // ==================================================== ( constructor init )
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TasksEventProducer(

        KafkaTemplate<String, byte[]> kafkaTemplate,
        ObjectMapper objectMapper

    ) {

        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;

    }
    // ===================================================== ( constructor end )

    // create update producer
    public void producerCreateUpdate(

        TasksEntity message

    ) {

        try {

            // Convert object to JSON bytes
            byte[] payload = objectMapper.writeValueAsBytes(message);

            // Send as raw bytes
            kafkaTemplate.send(
                TasksMessagingTopicEnum.TASKS_CREATE_UPDATE_PERSIST,
                payload
            ).get(10, TimeUnit.SECONDS);

        } catch (Exception e) {

            throw new InternalError(
                "Error while producing the Kafka message " +
                "[ TasksEventProducer.producerCreateUpdate() ]: " + e
            );

        }

    }

    // delete producer
    public void producerDelete (

        TasksEntity message

    ) {

        try {

            // Convert object to JSON bytes
            byte[] payload = objectMapper.writeValueAsBytes(message);

            // Send as raw bytes
            kafkaTemplate.send(
                TasksMessagingTopicEnum.TASKS_DELETE_PERSIST,
                payload
            ).get(10, TimeUnit.SECONDS);

        } catch (Exception e) {

            throw new InternalError(
                "Error while producing the Kafka message " +
                "[ TasksEventProducer.producerDelete() ]: " + e
            );

        }

    }

}