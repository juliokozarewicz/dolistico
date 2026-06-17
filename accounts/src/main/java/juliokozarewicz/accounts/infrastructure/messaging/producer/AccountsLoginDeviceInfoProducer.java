package juliokozarewicz.accounts.infrastructure.messaging.producer;

import juliokozarewicz.accounts.application.command.AccountsDeviceSessionsCommand;
import juliokozarewicz.accounts.application.command.AccountsSendEmailCommand;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.messaging.enums.AccountsMessagingTopicEnum;
import juliokozarewicz.accounts.infrastructure.shared.AccountsDeviceExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class AccountsLoginDeviceInfoProducer {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${APPLICATION_TITLE}")
    private String applicationTitle;

    @Value("${PUBLIC_DOMAIN}")
    private String publicDomain;
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsLoginDeviceInfoProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;
    private final AccountsDeviceExtractor accountsDeviceExtractor;

    public AccountsLoginDeviceInfoProducer(

        KafkaTemplate<String, byte[]> kafkaTemplate,
        ObjectMapper objectMapper,
        MessageSource messageSource,
        AccountsDeviceExtractor accountsDeviceExtractor

    ) {

        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
        this.accountsDeviceExtractor = accountsDeviceExtractor;

    }

    // ===================================================== ( constructor end )

    // Send simple email producer
    public void execute(

        String userIp,
        String userAgent,
        Locale locale,
        String userId,
        String email,
        String method

    ) {

        try {

            // Location
            String loginLocation = String.valueOf(accountsDeviceExtractor.getLocationByIp(userIp, locale).get("description"));

            // Device
            String loginDevice = String.valueOf(accountsDeviceExtractor.getDeviceByUserAgent(locale, userAgent).get("description"));

            // Login time
            Instant loginInstant = Instant.now();
            String loginTime = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'")
                .withZone(ZoneOffset.UTC)
                .format(loginInstant);

            // Main template
            ClassPathResource templateResource = new ClassPathResource(
                "templates/email/AccountsLoginDeviceInfo.html"
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
                    "{{emailAddress}}",
                    email
                )

                .replace(
                    "{{locationData}}",
                    loginLocation
                )

                .replace(
                    "{{deviceData}}",
                    loginDevice
                )

                .replace(
                    "{{loginTime}}",
                    loginTime
                )

                .replace(
                    "{{email_new_login_method_message}}",
                    method
                )

                .replace(
                    "{{publicDomain}}",
                    publicDomain.split(",")[0].trim()
                )

                .replace(
                    "{{language}}",
                    locale.getLanguage()
                )

                .replace(
                    "{{email_new_login_header}}",
                    messageSource.getMessage("email_new_login_header", null, locale)
                )

                .replace(
                    "{{email_new_login_message_one}}",
                    messageSource.getMessage("email_new_login_message_one", null, locale)
                )

                .replace(
                    "{{email_new_login_message_two}}",
                    messageSource.getMessage("email_new_login_message_two", null, locale)
                )

                .replace(
                    "{{email_new_login_device_title}}",
                    messageSource.getMessage("email_new_login_device_title", null, locale)
                )

                .replace(
                    "{{email_new_login_account}}",
                    messageSource.getMessage("email_new_login_account", null, locale)
                )

                .replace(
                    "{{email_new_login_method}}",
                    messageSource.getMessage("email_new_login_method", null, locale)
                )

                .replace(
                    "{{email_new_login_location}}",
                    messageSource.getMessage("email_new_login_location", null, locale)
                )

                .replace(
                    "{{email_new_login_Device}}",
                    messageSource.getMessage("email_new_login_device", null, locale)
                )

                .replace(
                    "{{email_new_login_time}}",
                    messageSource.getMessage("email_new_login_time", null, locale)
                )

                .replace(
                    "{{email_new_login_application}}",
                    messageSource.getMessage("email_new_login_application", null, locale)
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

            // --------------------------------------------- ( send email init )

            // Create JSON payload
            AccountsSendEmailCommand emailMessageMap = new AccountsSendEmailCommand(
                email,
                messageSource.getMessage("email_new_login_subject", null, locale),
                message.toString()
            );

            // Convert object to bytes
            byte[] payloadEmail = objectMapper.writeValueAsBytes(emailMessageMap);

            // Send as raw bytes
            kafkaTemplate.send(
                AccountsMessagingTopicEnum.SEND_SIMPLE_EMAIL,
                payloadEmail
            ).get(5, TimeUnit.SECONDS);

            // --------------------------------------------- ( send email init )

            // ------------------------------------------ ( create device init )

            // Create JSON payload
            AccountsDeviceSessionsCommand deviceCommand = new AccountsDeviceSessionsCommand(
                UUID.randomUUID(),
                UUID.fromString(userId),
                loginInstant,
                userIp,
                loginLocation,
                loginDevice,
                method
            );

            // Convert object to bytes
            byte[] payloadDevice = objectMapper.writeValueAsBytes(deviceCommand);

            // Send as raw bytes
            kafkaTemplate.send(
                AccountsMessagingTopicEnum.ACCOUNTS_CREATE_LOGIN_DEVICE,
                payloadDevice
            ).get(5, TimeUnit.SECONDS);

            // ------------------------------------------ ( create device init )

        } catch (Exception e) {

            // Logs
            logger.atError()
            .addKeyValue("topicEmail", AccountsMessagingTopicEnum.SEND_SIMPLE_EMAIL)
            .addKeyValue("topicDevice", AccountsMessagingTopicEnum.ACCOUNTS_CREATE_LOGIN_DEVICE)
            .log("Error producing message: [ AccountsLoginDeviceInfoProducer.execute() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}