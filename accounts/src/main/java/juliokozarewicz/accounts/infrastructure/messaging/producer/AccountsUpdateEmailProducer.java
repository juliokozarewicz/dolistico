package juliokozarewicz.accounts.infrastructure.messaging.producer;

import juliokozarewicz.accounts.application.command.AccountsSendEmailCommand;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.messaging.enums.AccountsMessagingTopicEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Component
public class AccountsUpdateEmailProducer {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${APPLICATION_TITLE}")
    private String applicationTitle;

    @Value("${PUBLIC_DOMAIN}")
    private String publicDomain;
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsUpdateEmailProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    public AccountsUpdateEmailProducer(

        KafkaTemplate<String, byte[]> kafkaTemplate,
        ObjectMapper objectMapper,
        MessageSource messageSource

    ) {

        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;

    }

    // ===================================================== ( constructor end )

    // ========================================================= ( helper init )
    private void sendEmail(
        Locale locale,
        String email,
        String subjectKey,
        String messageKey
    ) throws Exception {

        // Main template
        ClassPathResource templateResource = new ClassPathResource(
            "templates/email/AccountsEmailUpdated.html"
        );

        String message = new String(
            templateResource.getInputStream().readAllBytes(),
            StandardCharsets.UTF_8
        );

        // Footer template
        ClassPathResource footerResource = new ClassPathResource(
            "templates/email/Footer.html"
        );

        String footer = new String(
            footerResource.getInputStream().readAllBytes(),
            StandardCharsets.UTF_8
        );

        // Replace footer
        message = message.replace("{{footer}}", footer);

        // Replace variables
        message = message

            .replace(
                "{{publicDomain}}",
                publicDomain.split(",")[0].trim()
            )

            .replace(
                "{{language}}",
                locale.getLanguage()
            )

            .replace(
                "{{email_greeting}}",
                messageSource.getMessage(
                    "email_greeting",
                    null,
                    locale
                )
            )

            .replace(
                "{{update_email_message}}",
                messageSource.getMessage(
                    messageKey,
                    null,
                    locale
                )
            )

            .replace(
                "{{email_closing}}",
                messageSource.getMessage(
                    "email_closing",
                    null,
                    locale
                )
            )

            .replace(
                "{{applicationTitle}}",
                applicationTitle.toUpperCase()
            )

            .replace(
                "{{email_footer_message}}",
                messageSource.getMessage(
                    "email_footer_message",
                    null,
                    locale
                )
            );

        AccountsSendEmailCommand command =
            new AccountsSendEmailCommand(
                email,
                messageSource.getMessage(
                    subjectKey,
                    null,
                    locale
                ),
                message
            );

        byte[] payload =
            objectMapper.writeValueAsBytes(command);

        kafkaTemplate.send(
            AccountsMessagingTopicEnum.SEND_SIMPLE_EMAIL,
            payload
        ).get(5, TimeUnit.SECONDS);

    }
    // ========================================================= ( helper init )

    // Send simple email producer
    public void execute(

        Locale locale,
        String oldEmail,
        String newEmail

    ) {

        try {

            sendEmail(
                locale,
                oldEmail,
                "email_account_update_email_subject",
                "email_account_updated_old_email_message"
            );

            sendEmail(
                locale,
                newEmail,
                "email_account_update_email_subject",
                "email_account_updated_new_email_message"
            );

        } catch (Exception e) {

            // Logs
            logger.atError()
            .addKeyValue("topic", AccountsMessagingTopicEnum.SEND_SIMPLE_EMAIL)
            .log("Error producing message: [ AccountsWelcomeProducer.execute() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}