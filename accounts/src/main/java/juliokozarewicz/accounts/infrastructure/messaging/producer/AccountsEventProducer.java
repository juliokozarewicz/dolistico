package juliokozarewicz.accounts.infrastructure.messaging.producer;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.messaging.enums.AccountsMessagingGroupEnum;
import juliokozarewicz.accounts.infrastructure.messaging.enums.AccountsMessagingTopicEnum;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

public class AccountsEventProducer {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsEventProducer.class);

    public AccountsEventProducer (

    ) {

    }

    // ===================================================== ( constructor end )

    // Delete account not activated producer
    @KafkaListener(
        topics = AccountsMessagingTopicEnum.ACCOUNTS_NOT_ACTIVATED_DELETE,
        groupId = AccountsMessagingGroupEnum.ACCOUNTS_GROUP_ID
    )
    public void producerDeleteAccountNotActivated (

        ConsumerRecord<String, byte[]> record,
        Acknowledgment ack

    ) {

        try {

            // Create message
            byte[] payload = record.value();

            ack.acknowledge();

        } catch (Exception e) {

            // Logs
            logger.atError()
            .addKeyValue("topic", AccountsMessagingTopicEnum.ACCOUNTS_NOT_ACTIVATED_DELETE)
            .log("Error producing message: [ AccountsEventProducer.producerDeleteAccountNotActivated() ]", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}
