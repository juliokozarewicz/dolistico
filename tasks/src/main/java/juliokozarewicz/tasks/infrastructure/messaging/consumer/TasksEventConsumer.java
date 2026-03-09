package juliokozarewicz.tasks.infrastructure.messaging.consumer;

import juliokozarewicz.tasks.domain.entity.TasksEntity;
import juliokozarewicz.tasks.domain.repository.TasksRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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

     // consumer
    @KafkaListener(
        topics = "#{T(juliokozarewicz.tasks.infrastructure.messaging.enums.MessagingTopicEnum).TASKS_CREATE_PERSIST.getTopicName()}",
        groupId = "#{T(juliokozarewicz.tasks.infrastructure.messaging.enums.MessagingTopicEnum).TASKS_CREATE_PERSIST.getTopicName()}"
    )
    public void consumer(

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

            throw new InternalError(
                "Error while consuming the Kafka message " +
                "[ TasksEventConsumer.consumer() ]: " + e
            );

        }

    }

}