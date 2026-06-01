package juliokozarewicz.accounts.infrastructure.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class Encryption {

    // ====================================================== (Constructor init)

    // Keys
    // -------------------------------------------------------------------------
    @Value("${ACCOUNTS_SECRET_KEY}")
    private String AccountsSecretKey;
    // -------------------------------------------------------------------------

    private static final SecureRandom secureRandom = new SecureRandom();
    private SecretKey aesKey;

    // ======================================================= (Constructor end)

    // =================================================== (Post construct init)
    @PostConstruct
    private void init() {

        try {

            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = sha.digest(AccountsSecretKey.getBytes(StandardCharsets.UTF_8));
            this.aesKey = new SecretKeySpec(keyBytes, "AES");

        } catch (Exception e) {

            throw new SecurityException("Failed to initialize AES key [ Encryption.init() ]: ", e);

        }

    }
    // ==================================================== (Post construct end)

    // ======================================================= (encryption init)
    public String encrypt(String plainText) {

        try {

            byte[] iv = new byte[12];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);

            byte[] ciphertext = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] encryptedData = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, encryptedData, 0, iv.length);
            System.arraycopy(ciphertext, 0, encryptedData, iv.length, ciphertext.length);

            return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedData);

        } catch (Exception e) {

            throw new SecurityException("Error encrypting [ Encryption.encrypt() ]: ");

        }

    }
    // ======================================================== (encryption end)

    // ========================================================== (decrypt init)
    public String decrypt(String encryptedText) {

        try {

            byte[] encryptedData = Base64.getUrlDecoder().decode(encryptedText);

            byte[] iv = new byte[12];
            byte[] ciphertext = new byte[encryptedData.length - iv.length];

            System.arraycopy(encryptedData, 0, iv, 0, iv.length);
            System.arraycopy(encryptedData, iv.length, ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

            byte[] decryptedBytes = cipher.doFinal(ciphertext);
            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {

            throw new SecurityException("Error decrypting [ Encryption.decrypt() ]");

        }

    }
    // =========================================================== (decrypt end)

    // ===================================================== (Create token init)
    public String generate512Hex() {

        byte[] bytes = new byte[256];
        secureRandom.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();

    }
    // ====================================================== (Create token end)

    // ======================================================= (Create pin init)
    public String generatePin() {

        int pin = secureRandom.nextInt(1_000_000);
        return String.format("%06d", pin);

    }
    // ======================================================== (Create pin end)

}