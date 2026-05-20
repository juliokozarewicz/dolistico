package juliokozarewicz.accounts.infrastructure.messaging.producer;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.messaging.enums.AccountsMessagingTopicEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class AccountsUpdatePasswordProducer {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${APPLICATION_TITLE}")
    private String applicationTitle;
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsUpdatePasswordProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    public AccountsUpdatePasswordProducer(

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
        String email,
        String commandURL

    ) {

        try {

            // Email message
            StringBuilder message = new StringBuilder();

            message.append("<html>")
                .append("<body style='")
                    .append("margin:0;")
                    .append("padding:0;")
                    .append("width:100%;")
                    .append("font-family:Arial;")
                    .append("background:#f5f5f5;")
                .append("'>")

                    // Container principal
                    .append("<div style='")
                        .append("width:100%;")
                        .append("max-width:100%;")
                        .append("box-sizing:border-box;")
                        .append("background:#ffffff;")
                    .append("'>")

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
                            .append("box-sizing:border-box;")
                            .append("background:#000000;")
                            .append("text-align:center;")
                            .append("color:#ffffff;")
                            .append("text-decoration:none;")
                            .append("font-weight:bold;")
                            .append("border-radius:6px;")
                            .append("font-size:15px;")
                            .append("padding-top:10px;")
                            .append("padding-right:10px;")
                            .append("padding-bottom:10px;")
                            .append("padding-left:10px;")
                            .append("'>")
                            .append(messageSource.getMessage("email_click_here_link", null, locale))
                        .append("</a>")

                        .append("<br/><br/>")

                        .append("<p>")
                            .append(messageSource.getMessage("email_closing", null, locale))
                        .append("</p>")

                    .append("</div>")

                    // Footer
                    .append("<div style='")
                        .append("display:block;")
                        .append("border-radius:6px;")
                        .append("width:100%;")
                        .append("background:#000000;")
                        .append("color:#ffffff;")
                    .append("'>")

                        .append("<div style='")
                            .append("font-weight:600;")
                            .append("padding-top:30px;")
                            .append("padding-left:5px;")
                            .append("font-size:18px;")
                            .append("'>")
                            .append(applicationTitle.toUpperCase())
                        .append("</div>")

                        .append("<div style='")
                            .append("font-size:10px;")
                            .append("padding-top:10px;")
                            .append("padding-left:5px;")
                            .append("padding-bottom:30px;")
                            .append("margin-bottom:30px;")
                            .append("opacity:0.7;")
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
                    "email_subject_update_password",
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
            .log("Error producing message: [ AccountsUpdatePasswordProducer.execute() ]", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}
