package juliokozarewicz.accounts.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class AuthenticationFilter extends OncePerRequestFilter {

// ========================================================== (Constructor init)

    // Env
    // -------------------------------------------------------------------------
    @Value("${ACCOUNTS_SECRET_KEY}")
    private String secretKey;

    @Value("${ACCOUNTS_BASE_URL}")
    private String accountsBaseURL;
    // -------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private SecretKey aesKey;
    private List<String> publicPaths;

// =========================================================== (Constructor end)

// ======================================================= (Post construct init)

    @PostConstruct
    private void init() {

        // -------------------------------------------- ( Public endpoints init)
        publicPaths = Arrays.asList(
            "/actuator/**",
            "/" + accountsBaseURL + "/create",
            "/" + accountsBaseURL + "/password/update/request",
            "/" + accountsBaseURL + "/password/update/confirm",
            "/" + accountsBaseURL + "/login/request",
            "/" + accountsBaseURL + "/login/confirm",
            "/" + accountsBaseURL + "/login/refresh",
            "/" + accountsBaseURL + "/logout",
            "/" + accountsBaseURL + "/email/update/confirm",
            "/" + accountsBaseURL + "/delete/confirm"
        );
        // --------------------------------------------- ( Public endpoints end)

        try {

            // AES key
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = sha.digest(secretKey.getBytes(StandardCharsets.UTF_8));
            this.aesKey = new SecretKeySpec(keyBytes, "AES");

        } catch (Exception e) {
            throw new SecurityException("Failed to initialize AuthenticationFilter keys [AuthenticationFilter.init()]");
        }

    }

    public List<String> getPublicPaths() {
        return new ArrayList<>(publicPaths);
    }

// ======================================================== (Post construct end)

// ==================================================== (Assistant methods init)

    public String decrypt(String encryptedText)  throws Exception {
            byte[] encryptedData = Base64.getUrlDecoder().decode(encryptedText);
            byte[] iv = new byte[12];
            byte[] ciphertext = new byte[encryptedData.length - iv.length];
            System.arraycopy(encryptedData, 0, iv, 0, iv.length);
            System.arraycopy(encryptedData, iv.length, ciphertext, 0, ciphertext.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(128, iv));
            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
    }

    private void buildErrorResponse(

        HttpServletResponse response,
        int statusCode,
        String messageCode

    ) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        body.put("statusCode", statusCode);
        body.put("messageCode", messageCode);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

// ===================================================== (Assistant methods end)

    @Override
    protected void doFilterInternal(

        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain

    ) throws RuntimeException, IOException {

        try {

            // ---------------------------------- (Route not authenticated init)
            String requestPath = request.getRequestURI();

            boolean isPublic = publicPaths.stream()
                .anyMatch(p -> pathMatcher.match(p, requestPath));

            if (isPublic) {
                filterChain.doFilter(request, response);
                return;
            }
            // ----------------------------------- (Route not authenticated end)

            // ------------------------ (Replace jwt crypted for decrypted init)
            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String encrypted = header.substring(7);

            // Decrypt - return invalid credentials if fail
            String decryptedJwt;

            try {
                decryptedJwt = decrypt(encrypted);
            } catch (Exception e) {
                buildErrorResponse(response, 401, "INVALID_CREDENTIALS");
                return;
            }

            // Expired token verification
            try {
                SignedJWT jwt = SignedJWT.parse(decryptedJwt);
                Date exp = jwt.getJWTClaimsSet().getExpirationTime();
                if (exp != null && exp.before(new Date())) {
                    buildErrorResponse(response, 401, "ACCESS_EXPIRED");
                    return;
                }
            } catch (Exception e) {
                buildErrorResponse(response, 401, "INVALID_CREDENTIALS");
                return;
            }

            request.setAttribute("DECRYPTED_JWT", decryptedJwt);

            filterChain.doFilter(request, response);
            // ------------------------- (Replace jwt crypted for decrypted end)

        } catch (Exception e) {
            log.error("Unexpected error in AuthenticationFilter [AuthenticationFilter.doFilterInternal()] : ", e);
            buildErrorResponse(response, 500, "INTERNAL_SERVER_ERROR");
        }

    }

}