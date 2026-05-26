package juliokozarewicz.tasks.infrastructure.messaging.producer;


import juliokozarewicz.tasks.domain.entity.TasksEntity;
import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;
import juliokozarewicz.tasks.infrastructure.messaging.enums.TasksMessagingTopicEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Component
public class TasksEventProducer {

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    // ==================================================== ( constructor init )
    private static final Logger logger = LoggerFactory.getLogger(TasksEventProducer.class);
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
            ).get(5, TimeUnit.SECONDS);

        } catch (Exception e) {

            // logs
            logger.atError()
            .addKeyValue("topic", TasksMessagingTopicEnum.TASKS_CREATE_UPDATE_PERSIST)
            .log("Error producing message: [ TasksEventProducer.producerCreateUpdate() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

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
            ).get(5, TimeUnit.SECONDS);

        } catch (Exception e) {

            // Logs
            logger.atError()
            .addKeyValue("topic", TasksMessagingTopicEnum.TASKS_DELETE_PERSIST)
            .log("Error producing message: [ TasksEventProducer.producerDelete() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}