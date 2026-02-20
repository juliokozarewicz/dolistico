package juliokozarewicz.accounts.services;

import juliokozarewicz.accounts.dtos.SendEmailDataDTO;
import juliokozarewicz.accounts.enums.KafkaTopicEnum;
import juliokozarewicz.accounts.exceptions.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
public class AccountsKafkaService {

    // ==================================================== ( constructor init )
    private final MessageSource messageSource;
    private final ErrorHandler errorHandler;
    private static final Logger logger = LoggerFactory.getLogger(AccountsKafkaService.class);
    private final KafkaTemplate<String, SendEmailDataDTO> kafkaTemplate;

    public AccountsKafkaService(

        MessageSource messageSource,
        ErrorHandler errorHandler,
        KafkaTemplate<String, SendEmailDataDTO> kafkaTemplate

    ) {

        this.messageSource = messageSource;
        this.errorHandler = errorHandler;
        this.kafkaTemplate = kafkaTemplate;

    }
    // ===================================================== ( constructor end )

    // producer
    public void sendSimpleEmailMessage(

        SendEmailDataDTO sendEmailDataDTO

    ) {

        // language
        Locale locale = LocaleContextHolder.getLocale();

        try {

            kafkaTemplate.send(

                KafkaTopicEnum.SEND_SIMPLE_EMAIL,
                sendEmailDataDTO

            ).get(10, TimeUnit.SECONDS);

        } catch (Exception e) {

            // logs
            logger.error("Error creating message for broker in account " +
                "service [ AccountsKafkaService.sendSimpleEmailMessage() ]: " + e);

            errorHandler.customErrorThrow(
                500,
                messageSource.getMessage(
                    "response_kafka_offline_email", null, locale
                )
            );

        }

    }

}