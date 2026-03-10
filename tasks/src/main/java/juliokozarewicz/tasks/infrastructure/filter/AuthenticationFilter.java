package juliokozarewicz.tasks.infrastructure.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class AuthenticationFilter extends OncePerRequestFilter {

// ========================================================== (Constructor init)

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private SecretKey aesKey;
    private PublicKey rsaPublicKey;
    private List<String> publicPaths;

    @Value("${SECRET_KEY}")
    private String secretKey;

    @Value("${PUBLIC_KEY}")
    private String publicKey;

    public AuthenticationFilter() {}

// =========================================================== (Constructor end)

// ======================================================= (Post construct init)

    @PostConstruct
    private void init() {

        // -------------------------------------------- ( Public endpoints init)
        publicPaths = Arrays.asList(
            "/actuator/**"
        );
        // --------------------------------------------- ( Public endpoints end)

        try {

            // AES key
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = sha.digest(secretKey.getBytes(StandardCharsets.UTF_8));
            this.aesKey = new SecretKeySpec(keyBytes, "AES");

            // RSA public key (criada uma vez só)
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.rsaPublicKey = keyFactory.generatePublic(keySpec);

        } catch (Exception e) {
            throw new SecurityException("Failed to initialize AuthenticationFilter keys");
        }

    }

    public List<String> getPublicPaths() {
        return new ArrayList<>(publicPaths);
    }

// ======================================================== (Post construct end)

// ==================================================== (Assistant methods init)

    public Claims parseAndValidateToken(String token) {

        try {

            Jws<Claims> parsedJwt = Jwts.parser()
                .verifyWith(rsaPublicKey)
                .clockSkewSeconds(30)
                .build()
                .parseSignedClaims(token);

            String alg = parsedJwt.getHeader().getAlgorithm();

            if (!"RS256".equals(alg)) {
                throw new SecurityException("Invalid JWT algorithm: " + alg);
            }

            return parsedJwt.getPayload();

        } catch (ExpiredJwtException e) {
            throw new SecurityException("ACCESS_EXPIRED");
        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            throw new SecurityException("Invalid JWT");
        }

    }

    public String decrypt(String encryptedText) {

        try {

            byte[] encryptedData = Base64.getUrlDecoder().decode(encryptedText);
            byte[] iv = new byte[12];
            byte[] ciphertext = new byte[encryptedData.length - iv.length];

            System.arraycopy(encryptedData, 0, iv, 0, iv.length);
            System.arraycopy(encryptedData, iv.length, ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(128, iv));

            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.warn("Token decryption failed");
            throw new SecurityException("Error decrypting token");
        }

    }

    private void buildErrorResponse(

        HttpServletResponse response, int statusCode, String messageCode

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

            boolean isNotProtected = publicPaths.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestPath));

            if (isNotProtected) {
                filterChain.doFilter(request, response);
                return;
            }
            // ----------------------------------- (Route not authenticated end)

            // -------------------------------------- (Get jwt from header init)
            String accessCredentialRaw = request.getHeader("Authorization");
            String accessCredential = accessCredentialRaw != null
                ? accessCredentialRaw.replace("Bearer ", "")
                : null;

            if (
                accessCredential == null ||
                accessCredential.isBlank() ||
                accessCredential.length() > 4096
            ) {
                buildErrorResponse(response, 401, "INVALID_CREDENTIALS");
                return;
            }
            // --------------------------------------- (Get jwt from header end)

            // --------------------------------------------- (Validate JWT init)
            Claims claims;
            try {

                claims = parseAndValidateToken(decrypt(accessCredential));

            } catch (SecurityException e) {

                if ("ACCESS_EXPIRED".equals(e.getMessage())) {
                    buildErrorResponse(
                        response, 401, "ACCESS_EXPIRED"
                    );
                } else {
                    buildErrorResponse(
                        response, 401, "INVALID_CREDENTIALS"
                    );
                }
                return;

            }
            // ---------------------------------------------- (Validate JWT end)

            // ---------------------------------------------- (Claim's map init)
            if (claims.get("id") == null ||
                claims.get("email") == null ||
                claims.get("level") == null) {
                buildErrorResponse(response, 401, "INVALID_CREDENTIALS");
                return;
            }

            Object idUser    = claims.get("id");
            String emailUser = claims.get("email", String.class);
            String levelUser = claims.get("level", String.class);
            // ----------------------------------------------- (Claim's map end)

            List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + levelUser.toUpperCase())
            );

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(emailUser, null, authorities);

            Map<String, Object> dataMap = new LinkedHashMap<>();
            dataMap.put("id", idUser);
            dataMap.put("email", emailUser);
            dataMap.put("level", levelUser);

            request.setAttribute("credentialsData", dataMap);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Unexpected error in AuthenticationFilter", e);
            buildErrorResponse(response, 500, "INTERNAL_SERVER_ERROR");
        }

    }

}