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
public class AccountsWelcomeProducer {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${APPLICATION_TITLE}")
    private String applicationTitle;

    @Value("${PUBLIC_DOMAIN}")
    private String publicDomain;
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsWelcomeProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    public AccountsWelcomeProducer(

        KafkaTemplate<String, byte[]> kafkaTemplate,
        ObjectMapper objectMapper,
        MessageSource messageSource

    ) {

        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;

    }

    // ===================================================== ( constructor end )

    // Send simple email producer
    public void execute(

        Locale locale,
        String email

    ) {

        try {

            // Main template
            ClassPathResource templateResource = new ClassPathResource(
                    "templates/email/AccountsWelcome.html"
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

            // Replace footer first
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
                            messageSource.getMessage("email_greeting", null, locale)
                    )

                    .replace(
                            "{{email_account_welcome_message}}",
                            messageSource.getMessage("email_account_welcome_message", null, locale)
                    )

                    .replace(
                            "{{email_closing}}",
                            messageSource.getMessage("email_closing", null, locale)
                    )

                    .replace(
                            "{{applicationTitle}}",
                            applicationTitle.toUpperCase()
                    )

                    .replace(
                            "{{email_footer_message}}",
                            messageSource.getMessage("email_footer_message", null, locale)
                    );

            // Create JSON payload
            AccountsSendEmailCommand emailMessageMap = new AccountsSendEmailCommand(
                email,
                messageSource.getMessage("email_account_welcome_subject", null, locale),
                message.toString()
            );

            // Convert object to bytes
            byte[] payload = objectMapper.writeValueAsBytes(emailMessageMap);

            // Send as raw bytes
            kafkaTemplate.send(
                AccountsMessagingTopicEnum.SEND_SIMPLE_EMAIL,
                payload
            ).get(5, TimeUnit.SECONDS);

        } catch (Exception e) {

            // Logs
            logger.atError()
            .addKeyValue("topic", AccountsMessagingTopicEnum.SEND_SIMPLE_EMAIL)
            .log("Error producing message: [ AccountsWelcomeProducer.execute() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}