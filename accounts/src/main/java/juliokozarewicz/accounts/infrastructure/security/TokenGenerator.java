package juliokozarewicz.accounts.infrastructure.security;

import java.security.SecureRandom;

public class TokenGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String generate512Hex() {
        byte[] bytes = new byte[256];

        random.nextBytes(bytes);

        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();

    }

}