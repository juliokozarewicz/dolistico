package juliokozarewicz.tasks.infrastructure.messaging.consumer;

import juliokozarewicz.tasks.domain.entity.TasksEntity;
import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;
import juliokozarewicz.tasks.domain.repository.TasksRepository;
import juliokozarewicz.tasks.infrastructure.messaging.enums.TasksMessagingGroupEnum;
import juliokozarewicz.tasks.infrastructure.messaging.enums.TasksMessagingTopicEnum;
import juliokozarewicz.tasks.infrastructure.messaging.producer.TasksEventProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class TasksEventConsumer {

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    // ==================================================== ( constructor init )
    private static final Logger logger = LoggerFactory.getLogger(TasksEventConsumer.class);
    private final TasksRepository tasksRepository;
    private final ObjectMapper objectMapper;

    public TasksEventConsumer(

        TasksRepository tasksRepository,
        ObjectMapper objectMapper

    ) {

        this.tasksRepository = tasksRepository;
        this.objectMapper = objectMapper;

    }
    // ===================================================== ( constructor end )

     // create update consumer
    @KafkaListener(
        topics = TasksMessagingTopicEnum.TASKS_CREATE_UPDATE_PERSIST,
        groupId = TasksMessagingGroupEnum.ACCOUNTS_GROUP_ID
    )
    public void consumerCreateUpdate (

        ConsumerRecord<String, byte[]> record,
        Acknowledgment ack

    ) {

        try {

            // Save task entity in DB
            byte[] payload = record.value();
            TasksEntity tasksEntity = objectMapper.readValue(payload, TasksEntity.class);
            tasksRepository.save(tasksEntity);
            ack.acknowledge();

        } catch (Exception e) {

            logger.error("Error consuming message: [ TasksEventConsumer.consumerCreateUpdate() ]: " + e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

    // delete consumer
    @KafkaListener(
        topics = TasksMessagingTopicEnum.TASKS_DELETE_PERSIST,
        groupId = TasksMessagingGroupEnum.ACCOUNTS_GROUP_ID
    )
    public void consumerDelete (

        ConsumerRecord<String, byte[]> record,
        Acknowledgment ack

    ) {

        try {

            // Save task entity in DB
            byte[] payload = record.value();
            TasksEntity tasksEntity = objectMapper.readValue(payload, TasksEntity.class);
            tasksRepository.delete(tasksEntity);
            ack.acknowledge();

        } catch (Exception e) {

            logger.error("Error consuming message: [ TasksEventConsumer.TasksEventConsumer() ]: " + e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}