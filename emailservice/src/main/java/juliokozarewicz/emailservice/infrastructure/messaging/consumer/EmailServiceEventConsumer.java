package juliokozarewicz.emailservice.infrastructure.messaging.consumer;


import jakarta.mail.internet.MimeMessage;
import juliokozarewicz.emailservice.application.command.EmailServiceSendEmailCommand;
import juliokozarewicz.emailservice.domain.exception.DomainException;
import juliokozarewicz.emailservice.domain.exception.DomainExceptionEnum;
import juliokozarewicz.emailservice.infrastructure.messaging.enums.EmailServiceMessagingGroupEnum;
import juliokozarewicz.emailservice.infrastructure.messaging.enums.EmailServiceMessagingTopicEnum;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class EmailServiceEventConsumer {

    // Env
    // -------------------------------------------------------------------------
    @Value("${EMAIL_SERVICE_ADDRESS_USER}")
    private String fromEmail;

    @Value("${APPLICATION_TITLE}")
    private String applicationTitle;
    // -------------------------------------------------------------------------

    // ==================================================== ( constructor init )
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceEventConsumer.class);
    private final ObjectMapper objectMapper;
    private final JavaMailSender javaMailSender;

    public EmailServiceEventConsumer(

        ObjectMapper objectMapper,
        JavaMailSender javaMailSender

    ) {

        this.objectMapper = objectMapper;
        this.javaMailSender = javaMailSender;

    }
    // ===================================================== ( constructor end )

     // Send simple email
    @KafkaListener(
        topics = EmailServiceMessagingTopicEnum.SEND_SIMPLE_EMAIL,
        groupId = EmailServiceMessagingGroupEnum.EMAIL_SERVICE_GROUP_ID
    )
    public void consumerSendSimpleEmail (

        ConsumerRecord<String, byte[]> record,
        Acknowledgment ack

    ) {

        try {

            // Payload
            byte[] payload = record.value();

            // Deserialize
            EmailServiceSendEmailCommand emailMessageMap = objectMapper.readValue( payload, EmailServiceSendEmailCommand.class);
            String email = emailMessageMap.recipientEmail();
            String subject = emailMessageMap.subject();
            String message = emailMessageMap.messages();

            // Send simple email
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mailMessage = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mailMessage.setFrom(fromEmail, applicationTitle.toUpperCase());
            mailMessage.setTo(email);
            mailMessage.setSubject(subject);
            mailMessage.setText(message, true);
            javaMailSender.send(mimeMessage);

            ack.acknowledge();

        } catch (Exception e) {

            // Logs
            logger.atError()
            .addKeyValue("topic", record.topic())
            .addKeyValue("partition", record.partition())
            .addKeyValue("offset", record.offset())
            .log("Error consuming message: [ EmailServiceEventConsumer.consumerSendSimpleEmail() ]", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}