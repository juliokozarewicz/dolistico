package juliokozarewicz.accounts.infrastructure.messaging.consumer;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.domain.repository.AccountsProfileRepository;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakDeleteUser;
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
public class AccountsEventConsumer {

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    // ==================================================== ( constructor init )
    private static final Logger logger = LoggerFactory.getLogger(AccountsEventConsumer.class);
    private final ObjectMapper objectMapper;
    private final AccountsKeycloakDeleteUser accountsKeycloakDeleteUser;
    private final AccountsProfileRepository accountsProfileRepository;

    public AccountsEventConsumer (

        ObjectMapper objectMapper,
        AccountsKeycloakDeleteUser accountsKeycloakDeleteUser,
        AccountsProfileRepository accountsProfileRepository

    ) {

        this.objectMapper = objectMapper;
        this.accountsKeycloakDeleteUser = accountsKeycloakDeleteUser;
        this.accountsProfileRepository = accountsProfileRepository;

    }
    // ===================================================== ( constructor end )

     // Delete expired accounts
    @KafkaListener(
        topics = AccountsMessagingTopicEnum.ACCOUNTS_NOT_ACTIVATED_DELETE,
        groupId = AccountsMessagingGroupEnum.ACCOUNTS_GROUP_ID
    )
    public void consumerDeleteAccountNotActivated (

        ConsumerRecord<String, byte[]> record,
        Acknowledgment ack

    ) {

        try {

            // Payload
            byte[] payload = record.value();

            // Deserialize user id
            String userId = objectMapper.readValue(payload, String.class);

            // Delete expired account
            accountsKeycloakDeleteUser.execute(userId);

            // Get and delete expired account profile
            accountsProfileRepository.delete(userId);

            ack.acknowledge();

        } catch (Exception e) {

            // Logs
            logger.atError()
            .addKeyValue("topic", record.topic())
            .addKeyValue("partition", record.partition())
            .addKeyValue("offset", record.offset())
            .log("Error consuming message: [ AccountsEventConsumer.consumerDeleteAccountNotActivated() ]", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}