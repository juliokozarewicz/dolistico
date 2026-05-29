package juliokozarewicz.accounts.infrastructure.keycloak;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.Base64;
import java.util.Map;

@Service
public class AccountsKeycloakLoginService {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------

    @Value("${ACCOUNTS_KEYCLOAK_REALM}")
    private String keycloakRealm;

    @Value("${KEYCLOAK_BASE_URL}")
    private String keycloakBaseURL;

    @Value("${ACCOUNTS_KEYCLOAK_CLIENT_ID}")
    private String clientId;

    @Value("${ACCOUNTS_SECRET_KEY}")
    private String clientSecret;

    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsKeycloakLoginService.class);
    private final WebClient webClient;

    public AccountsKeycloakLoginService(

        WebClient webClient

    ) {

        this.webClient = webClient;

    }

    // ===================================================== ( constructor end )

    // ==================================================== ( internal helpers )

    // Keycloak token endpoint (OIDC)
    private URI tokenEndpoint() {
        return UriComponentsBuilder
            .fromUriString(keycloakBaseURL)
            .pathSegment("realms", keycloakRealm, "protocol", "openid-connect", "token")
            .build()
            .toUri();
    }

    // Build form-data for Keycloak token requests
    private MultiValueMap<String, String> baseFormData() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);

        if (clientSecret != null && !clientSecret.isBlank()) {
            formData.add("client_secret", clientSecret);
        }

        return formData;
    }

    // ================================================ ( internal helpers end )

    // Returns Keycloak token response (access_token + refresh_token)
    public Map<String, Object> createUserLogin(

        String userEmail,
        String userPassword

    ) {

        MultiValueMap<String, String> formData = baseFormData();
        formData.add("grant_type", "password");
        formData.add("username", userEmail);
        formData.add("password", userPassword);
        formData.add("scope", "openid");

        return webClient.post()
            .uri(tokenEndpoint())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .exchangeToMono(response -> {

                if (response.statusCode().is2xxSuccessful()) {
                    return response.bodyToMono(Map.class);
                }

                return response.bodyToMono(String.class)
                    .defaultIfEmpty("")
                    .doOnNext(body -> logger.error("Keycloak error: {}", body))
                    .thenReturn(null);
            })
            .block();
    }

    // Refresh tokens using a valid refresh_token
    public Map<String, Object> refreshUserLogin(String refreshToken) {

        try {

            MultiValueMap<String, String> formData = baseFormData();
            formData.add("grant_type", "refresh_token");
            formData.add("refresh_token", refreshToken);

            return webClient.post()
            .uri(tokenEndpoint())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromValue(formData))
            .retrieve()
            .bodyToMono(Map.class)
            .block();

        } catch (Exception e) {

            logger.atError()
            .addKeyValue("realm", keycloakRealm)
            .log("Error refreshing user login in Keycloak [ AccountsKeycloakLoginService.refreshUserLogin() ]", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

    // Extract user id from JWT token
    public String idUserExtract(

        String accessToken

    ) {

        try {

            if (accessToken == null || accessToken.isBlank()) { return null; }

            String[] parts = accessToken.split("\\.");

            if (parts.length < 2) { return null; }

            // Decode JWT payload (Base64URL)
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));

            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> payload = mapper.readValue(payloadJson, Map.class);

            return (String) payload.get("sub");

        } catch (Exception e) {

            logger.atError()
            .addKeyValue("realm", keycloakRealm)
            .log("Error extracting user id from JWT in Keycloak [ AccountsKeycloakLoginService.idUserExtract() ]", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}
