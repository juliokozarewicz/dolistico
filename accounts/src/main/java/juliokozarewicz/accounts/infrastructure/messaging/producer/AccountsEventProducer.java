package juliokozarewicz.accounts.infrastructure.messaging.producer;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.messaging.enums.AccountsMessagingTopicEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class AccountsEventProducer {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
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
    public void producerDeleteAccountNotActivated (

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
            .log("Error producing message: [ AccountsEventProducer.producerDeleteAccountNotActivated() ]", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

    // Send simple email producer
    public void producerSendEmailLink(

        Locale locale,
        String email,
        String commandURL

    ) {

        try {

            // Email message
            StringBuilder message = new StringBuilder();

            message.append("<html>")

                .append("<body style='font-family: Arial;'>")

                .append("<p>")
                    .append(messageSource.getMessage("email_greeting", null, locale))
                .append("</p>")

                .append("<br/>")

                .append("<p>")
                    .append(messageSource.getMessage("email_reset_password_click", null, locale))
                .append("</p>")

                .append("<a href='").append(commandURL).append("' ")
                    .append("style='")
                    .append("display:inline-block;")
                    .append("background:#000000;")
                    .append("color:#ffffff;")
                    .append("padding:12px 20px;")
                    .append("text-decoration:none;")
                    .append("border-radius:6px;")
                    .append("font-weight:600;")
                    .append("font-family:Arial,sans-serif;")
                    .append("'>")
                    .append(messageSource.getMessage("email_click_here_link", null, locale))
                .append("</a>")

                .append("<br/>")

                .append("<p>")
                .append(messageSource.getMessage("email_closing", null, locale))
                .append("</p>")

                .append("<div style='")
                    .append("width:100%;")
                    .append("background:#000000;")
                    .append("color:#ffffff;")
                    .append("padding:16px 20px;")
                    .append("font-family:Arial,sans-serif;")
                    .append("font-size:12px;")
                    .append("line-height:1.5;")
                    .append("'>")

                    .append("<div style='")
                        .append("font-weight:600;")
                        .append("margin-bottom:6px;")
                        .append("'>")
                        .append("Nome da Empresa")
                    .append("</div>")

                    .append("<div style='")
                        .append("margin-bottom:6px;")
                        .append("'>")
                        .append("https://seusite.com")
                    .append("</div>")

                    .append("<div style='")
                        .append("opacity:0.85;")
                        .append("'>")
                        .append(messageSource.getMessage("email_footer_message", null, locale))
                    .append("</div>")

                .append("</div>")

                .append("</body>")

            .append("</html>");

            // Create JSON payload
            Map<String, String> emailMessageMap = new LinkedHashMap<>();
            emailMessageMap.put("recipientEmail", email);
            emailMessageMap.put(
                "subject",
                messageSource.getMessage(
                    "email_subject_account_service",
                    null,
                    locale
                )
            );
            emailMessageMap.put("message", message.toString());

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
            .log("Error producing message: [ AccountsEventProducer.producerSendEmailLink() ]", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}
