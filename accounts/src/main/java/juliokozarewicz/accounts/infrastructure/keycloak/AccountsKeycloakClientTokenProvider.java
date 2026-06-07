package juliokozarewicz.accounts.infrastructure.keycloak;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AccountsKeycloakClientTokenProvider {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    @Value("${ACCOUNTS_KEYCLOAK_REALM}")
    private String keycloakRealm;

    @Value("${ACCOUNTS_KEYCLOAK_CLIENT_ID}")
    private String keycloakClientId;

    @Value("${ACCOUNTS_KEYCLOAK_CLIENT_SECRET}")
    private String keycloakClientSecret;

    @Value("${KEYCLOAK_BASE_URL}")
    private String keycloakBaseURL;
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsKeycloakClientTokenProvider.class);
    private final WebClient webClient;
    private final CacheManager cacheManager;
    private final Cache clientKeycloakTokenCache;

    // Cache name
    private static final String cacheKey = "storedToken";

    public AccountsKeycloakClientTokenProvider (

        WebClient webClient,
        CacheManager cacheManager

    ) {

        this.webClient = webClient;
        this.cacheManager = cacheManager;
        this.clientKeycloakTokenCache = cacheManager.getCache("accounts.clientKeycloakTokenCache");

    }

    // ===================================================== ( constructor end )

    /**
     * Returns a valid access token.
     * Strategy:
     * 1. Try Redis cache
     * 2. If not present, fetch new token
     * 3. Store with dynamic TTL (based on expires_in - safety margin)
     */
    public synchronized String getAccessToken() {

        try {

            // Try Cache
            Cache.ValueWrapper cachedToken = clientKeycloakTokenCache.get(cacheKey);

            if (cachedToken != null) {
                return (String) ((LinkedHashMap<?, ?>) cachedToken.get()).get("clientToken");
            }

            // Build URL
            String url = keycloakBaseURL + "/realms/" + keycloakRealm + "/protocol/openid-connect/token";

            // Build request body
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", keycloakClientId);
            body.add("client_secret", keycloakClientSecret);
            body.add("grant_type", "client_credentials");

            // Call Keycloak
            Map<String, Object> response = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

            // Extract
            String clientToken = (String) response.get("access_token");

            // Map
            LinkedHashMap<String, String> tokenMap = new LinkedHashMap<>();
            tokenMap.put("clientToken", clientToken);

            // Store
            clientKeycloakTokenCache.put(cacheKey, tokenMap);

            // Return token
            return clientToken;

        } catch (Exception e) {

            // Logs
            logger.atError()
            .addKeyValue("realm", keycloakRealm)
            .addKeyValue("clientId", keycloakClientId)
            .addKeyValue("keycloakUrl", keycloakBaseURL)
            .log("Error accessing Keycloak [ AccountsKeycloakClientTokenProvider.getAccessToken() ] : ", e);

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

    }

}