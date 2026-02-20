package juliokozarewicz.accounts.services;

import juliokozarewicz.accounts.dtos.SendEmailDataDTO;
import juliokozarewicz.accounts.enums.KafkaTopicEnum;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AccountsKafkaService {

    // ==================================================== ( constructor init )
    private final KafkaTemplate<String, SendEmailDataDTO> kafkaTemplate;

    public AccountsKafkaService(

        KafkaTemplate<String, SendEmailDataDTO> kafkaTemplate

    ) {

        this.kafkaTemplate = kafkaTemplate;

    }
    // ===================================================== ( constructor end )

    // producer
    public void sendSimpleEmailMessage(

        SendEmailDataDTO sendEmailDataDTO

    ) {

        try {

            kafkaTemplate.send(KafkaTopicEnum.SEND_SIMPLE_EMAIL, sendEmailDataDTO)
                .get(10, TimeUnit.SECONDS);

        } catch (Exception e) {

            throw new InternalError("Error creating message for broker in account " +
                "service [ AccountsKafkaService.sendSimpleEmailMessage() ]: " + e);

        }

    }

}