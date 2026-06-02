package juliokozarewicz.accounts.infrastructure.messaging.producer;

import juliokozarewicz.accounts.application.command.AccountsCreateLogCommand;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.messaging.enums.AccountsMessagingTopicEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Component
public class AccountsEventProducer {

    // ==================================================== ( constructor init )

    // Env
    // ------------------ -------------------------------------------------------
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsEventProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    public AccountsEventProducer (

        KafkaTemplate<String, byte[]> kafkaTemplate,
        ObjectMapper objectMapper,
        MessageSource messageSource

    ) {

        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;

    }

    // ===================================================== ( constructor end )

    // Delete account not activated producer
    public void deleteAccountNotActivatedProducer (

        String idUser

    ) {

        try {

            // Convert object to bytes
            byte[] payload = objectMapper.writeValueAsBytes(idUser);

            // Send as raw bytes
            kafkaTemplate.send(
                AccountsMessagingTopicEnum.ACCOUNTS_NOT_ACTIVATED_DELETE,
                payload
            ).get(5, TimeUnit.SECONDS);

        } catch (Exception e) {

            // Logs
            logger.atError()
            .addKeyValue("topic", AccountsMessagingTopicEnum.ACCOUNTS_NOT_ACTIVATED_DELETE)
            .log("Error producing message: [ AccountsEventProducer.deleteAccountNotActivatedProducer() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

    // Account log producer
    public void accountLogProducer(

        AccountsCreateLogCommand accountsCreateLogCommand

    ) {

        try {

            // Convert object to bytes
            byte[] payload = objectMapper.writeValueAsBytes(accountsCreateLogCommand);

            // Send as raw bytes
            kafkaTemplate.send(
                AccountsMessagingTopicEnum.ACCOUNTS_CREATE_LOG,
                payload
            ).get(5, TimeUnit.SECONDS);

        } catch (Exception e) {

            // Logs
            logger.atError()
            .addKeyValue("topic", AccountsMessagingTopicEnum.ACCOUNTS_CREATE_LOG)
            .log("Error producing message: [ AccountsEventProducer.accountLogProducer() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}
