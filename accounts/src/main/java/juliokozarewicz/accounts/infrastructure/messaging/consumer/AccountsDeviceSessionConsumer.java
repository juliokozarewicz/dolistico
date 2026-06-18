package juliokozarewicz.accounts.infrastructure.messaging.consumer;

import juliokozarewicz.accounts.application.command.AccountsDeviceSessionsCommand;
import juliokozarewicz.accounts.domain.entity.AccountsDeviceSessionEntity;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.domain.repository.AccountsDeviceSessionRepository;
import juliokozarewicz.accounts.infrastructure.messaging.enums.AccountsMessagingGroupEnum;
import juliokozarewicz.accounts.infrastructure.messaging.enums.AccountsMessagingTopicEnum;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class AccountsDeviceSessionConsumer {

    // ==================================================== ( constructor init )
    private static final Logger logger =LoggerFactory.getLogger(AccountsDeviceSessionConsumer.class);
    private final ObjectMapper objectMapper;
    private final AccountsDeviceSessionRepository accountsDeviceSessionRepository;

    public AccountsDeviceSessionConsumer(

        ObjectMapper objectMapper,
        AccountsDeviceSessionRepository accountsDeviceSessionRepository

    ) {

        this.objectMapper = objectMapper;
        this.accountsDeviceSessionRepository = accountsDeviceSessionRepository;

    }
    // ===================================================== ( constructor ned )

    @KafkaListener(
        topics = AccountsMessagingTopicEnum.ACCOUNTS_CREATE_LOGIN_DEVICE,
        groupId = AccountsMessagingGroupEnum.ACCOUNTS_GROUP_ID
    )
    public void consumerCreateDeviceSession(

        ConsumerRecord<String, byte[]> record,
        Acknowledgment ack

    ) {

        try {

            // Payload
            byte[] payload = record.value();

            // Deserialize command
            AccountsDeviceSessionsCommand command = objectMapper.readValue(
                payload, AccountsDeviceSessionsCommand.class
            );

            // Entity
            AccountsDeviceSessionEntity entity = new AccountsDeviceSessionEntity(
                command.id(),
                command.idUser(),
                command.createdAt(),
                command.ipAddress(),
                command.location(),
                command.device(),
                command.method()
            );

            // Save
            accountsDeviceSessionRepository.save(entity);

            ack.acknowledge();

        } catch (Exception e) {

            logger.atError()
                .addKeyValue("topic", record.topic())
                .addKeyValue("partition", record.partition())
                .addKeyValue("offset", record.offset())
                .log("Error consuming message: [ AccountsDeviceSessionConsumer.consumerCreateDeviceSession() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}