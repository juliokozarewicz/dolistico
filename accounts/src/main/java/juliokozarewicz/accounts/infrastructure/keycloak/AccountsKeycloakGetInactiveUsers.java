package juliokozarewicz.accounts.infrastructure.keycloak;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class AccountsKeycloakGetInactiveUsers {

    // ==================================================== ( constructor init )

    // Environment variables
    // -------------------------------------------------------------------------
    @Value("${ACCOUNTS_KEYCLOAK_REALM}")
    private String keycloakRealm;

    @Value("${KC_BOOTSTRAP_ADMIN_USERNAME}")
    private String keycloakadminUser;

    @Value("${ACCOUNTS_KEYCLOAK_CLIENT_ID}")
    private String keycloakClientId;

    @Value("${KEYCLOAK_BASE_URL}")
    private String keycloakBaseURL;
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsKeycloakGetInactiveUsers.class);
    private final WebClient webClient;
    private final AccountsEventProducer accountsEventProducer;
    private final AccountsKeycloakClientTokenProvider accountsKeycloakClientTokenProvider;

    public AccountsKeycloakGetInactiveUsers(

        WebClient webClient,
        AccountsKeycloakClientTokenProvider accountsKeycloakClientTokenProvider,
        AccountsEventProducer accountsEventProducer

    ) {

        this.webClient = webClient;
        this.accountsKeycloakClientTokenProvider = accountsKeycloakClientTokenProvider;
        this.accountsEventProducer = accountsEventProducer;

    }

    // ===================================================== ( constructor end )

    public void getInactiveUsers() {

        try {

            // Login client Keycloak
            String clientToken = accountsKeycloakClientTokenProvider.getAccessToken();

            // Calculate timestamp for accounts created 7 days ago
            long sevenDaysAgoMillis = Instant.now()
                .minus(7, ChronoUnit.DAYS)
                .toEpochMilli();

            // Pagination config
            int first = 0;
            int max = 500;

            while (true) {

                // Build Keycloak users endpoint
                URI uri = UriComponentsBuilder
                    .fromUriString(keycloakBaseURL)
                    .pathSegment("admin", "realms", keycloakRealm, "users")
                    .queryParam("first", first)
                    .queryParam("max", max)
                    .queryParam("emailVerified", false)
                    .encode()
                    .build()
                    .toUri();

                // Fetch users page
                List<Map<String, Object>> users = webClient.get()
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + clientToken)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<List<Map<String, Object>>>() {})
                    .block();

                // Stop pagination if no users are returned
                if (users == null || users.isEmpty()) { break; }

                // Process users
                for (Map<String, Object> user : users) {

                    // If is system users
                    String username = (String) user.get("username");

                    if ( username != null &&

                        (
                            username.equals("service-account-" + keycloakClientId) ||
                            username.equals(keycloakadminUser)
                        )

                    ) {
                        continue;
                    }

                    String userId = (String) user.get("id");
                    Boolean emailVerified = (Boolean) user.get("emailVerified");
                    Number createdTimestampNumber = (Number) user.get("createdTimestamp");

                    long createdTimestamp = createdTimestampNumber != null
                        ? createdTimestampNumber.longValue()
                        : 0L;

                    // Rules:
                    // - email not verified
                    // - account created at least 7 days ago
                    if (

                        Boolean.FALSE.equals(emailVerified)
                        && createdTimestamp <= sevenDaysAgoMillis

                    ) {

                        // Create message
                        accountsEventProducer.producerDeleteAccountNotActivated(userId);

                    }

                }

                // Move to next page
                first += max;

            }

        } catch (Exception e) {

            // Logs
            logger.atError()
            .addKeyValue("realm", keycloakRealm)
            .log("Error getting inactive users in Keycloak [ AccountsKeycloakGetInactiveUsers.getInactiveUsers() ]", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}