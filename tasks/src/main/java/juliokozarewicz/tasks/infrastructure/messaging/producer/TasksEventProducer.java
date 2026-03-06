package juliokozarewicz.tasks.infrastructure.messaging.producer;


import juliokozarewicz.tasks.application.dto.TasksCreateMessageAppDTO;
import juliokozarewicz.tasks.infrastructure.messaging.enums.MessagingTopicEnum;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TasksEventProducer {

    // ==================================================== ( constructor init )
    private final KafkaTemplate<String, TasksCreateMessageAppDTO> kafkaTemplate;

    public TasksEventProducer(

        KafkaTemplate<String, TasksCreateMessageAppDTO> kafkaTemplate

    ) {

        this.kafkaTemplate = kafkaTemplate;

    }
    // ===================================================== ( constructor end )

    // producer
    public void publish(

        TasksCreateMessageAppDTO message

    ) {

        try {

            kafkaTemplate.send(
                MessagingTopicEnum.PERSIST_TASK_DATABASE.getTopicName(),
                message
            )
                .get(10, TimeUnit.SECONDS);

        } catch (Exception e) {

            throw new InternalError("Error creating message for broker " +
                "[ TasksEventProducer.TasksEventProducer() ]: " + e);

        }

    }

}
