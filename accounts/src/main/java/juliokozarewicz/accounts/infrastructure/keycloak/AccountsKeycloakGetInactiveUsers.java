package juliokozarewicz.accounts.infrastructure.keycloak;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
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

    @Value("${KEYCLOAK_BASE_URL}")
    private String keycloakBaseURL;
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsKeycloakGetInactiveUsers.class);
    private final WebClient webClient;

    public AccountsKeycloakGetInactiveUsers(

        WebClient webClient

    ) {

        this.webClient = webClient;

    }

    // ===================================================== ( constructor end )

    public void getInactiveUsers(String clientToken) {

        try {

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
                    .encode()
                    .build()
                    .toUri();

                // Fetch users page
                List<Map<String, Object>> users = webClient.get()
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + clientToken)
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();

                // Stop pagination if no users are returned
                if (users == null || users.isEmpty()) { break; }

                // Process users
                for (Map<String, Object> user : users) {

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

                        // ##### Temporary print
                        System.out.println("User to delete: " + userId);

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