package juliokozarewicz.accounts.infrastructure.security;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class TokenGenerator {

    private final SecureRandom random = new SecureRandom();

    public String generate512Hex() {
        byte[] bytes = new byte[256];

        random.nextBytes(bytes);

        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();

    }

    public String generatePin() {
        int pin = random.nextInt(10_000_000);
        return String.format("%06d", pin);
    }

}