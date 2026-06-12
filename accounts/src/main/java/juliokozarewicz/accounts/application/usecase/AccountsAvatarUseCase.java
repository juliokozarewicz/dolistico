package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.domain.entity.AccountsProfileEntity;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.domain.repository.AccountsProfileRepository;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AccountsAvatarUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------
    private final AccountsProfileRepository accountsProfileRepository;
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;
    private final Path uploadDir;
    private static final String DEFAULT_UPLOAD_DIR = "src/main/resources/static/public/uploads/avatar";

    public AccountsAvatarUseCase(

        AccountsProfileRepository accountsProfileRepository,
        AccountsKeycloakGetUser accountsKeycloakGetUser

    ) throws IOException {

        this.accountsProfileRepository = accountsProfileRepository;
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;
        this.uploadDir = Paths.get(DEFAULT_UPLOAD_DIR);
        Files.createDirectories(this.uploadDir);

    }

    // ===================================================== ( constructor end )

    public Map<String, Object> execute(

        UUID idUser,
        MultipartFile[] file

    ) {

        try {

            // Get profile
            AccountsProfileEntity profile = accountsProfileRepository.findByIdUser(idUser)
            .orElseThrow(() ->
                    new DomainException(DomainExceptionEnum.INVALID_CREDENTIALS)
            );

            // If user call the endpoint with nothing, delete the existing image
            // ---------------------------------------------------------------------
            String existingImagePath = profile.getAvatar();

            if (file == null || file.length == 0 || file[0].isEmpty()) {

                if (
                    existingImagePath != null &&
                    !existingImagePath.isBlank()
                ) {

                    String[] parts = existingImagePath.split("/");
                    String existingImageFilename = parts[parts.length - 1];
                    Path existingImageFullPath = uploadDir.resolve(existingImageFilename);
                    Files.deleteIfExists(existingImageFullPath);
                    profile.removeAvatar();
                    accountsProfileRepository.save(profile);

                }

                // Return response avatar null
                Map<String, Object> response =
                        new LinkedHashMap<>();

                response.put("avatar", null);

                return response;

            }
            // ---------------------------------------------------------------------

            // Only one image
            // ---------------------------------------------------------------------
            if (file.length != 1) {
                throw new DomainException(DomainExceptionEnum.ACCOUNTS_UPLOAD_AVATAR_ERROR);
            }
            // ---------------------------------------------------------------------

            // Images only
            // ---------------------------------------------------------------------
            String contentType = file[0].getContentType();

            if (
                contentType == null ||
                (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))
            ) {
                throw new DomainException(DomainExceptionEnum.ACCOUNTS_UPLOAD_AVATAR_ERROR);
            }
            // ---------------------------------------------------------------------

            // Image too large
            // ---------------------------------------------------------------------
            long maxSizeInBytes = 1 * 1024 * 1024;

            if (file[0].getSize() > maxSizeInBytes) {
                throw new DomainException(DomainExceptionEnum.ACCOUNTS_UPLOAD_AVATAR_ERROR);
            }
            // ---------------------------------------------------------------------

            // If user pass a new image, delete the existing one
            // ---------------------------------------------------------------------
            if (
                existingImagePath != null &&
                !existingImagePath.isBlank()
            ) {

                // Extract only the image filename
                String[] parts = existingImagePath.split("/");
                String existingImageFilename = parts[parts.length - 1];

                // Build full path to the existing image file
                Path existingImageFullPath = uploadDir.resolve(existingImageFilename);
                Files.deleteIfExists(existingImageFullPath);

            }
            // ---------------------------------------------------------------------

            // Image validation
            // ---------------------------------------------------------------------
            BufferedImage bufferedImage;

            try (InputStream inputStream = file[0].getInputStream()) {
                bufferedImage = ImageIO.read(inputStream);
            }

            if (bufferedImage == null) {
                throw new DomainException(DomainExceptionEnum.ACCOUNTS_UPLOAD_AVATAR_ERROR);
            }

            if (bufferedImage.getWidth() < 32 || bufferedImage.getHeight() < 32 ) {
                throw new DomainException(DomainExceptionEnum.ACCOUNTS_UPLOAD_AVATAR_ERROR);
            }

            if (bufferedImage.getWidth() > 4000 || bufferedImage.getHeight() > 4000) {
                throw new DomainException(DomainExceptionEnum.ACCOUNTS_UPLOAD_AVATAR_ERROR);
            }
            // ---------------------------------------------------------------------

            // Save image
            // ---------------------------------------------------------------------
            String generatedName = UUID.randomUUID() + ".png";
            Path targetPath = uploadDir.resolve(generatedName).normalize();

            if (!targetPath.startsWith(uploadDir)) {
                throw new DomainException(DomainExceptionEnum.ACCOUNTS_UPLOAD_AVATAR_ERROR);
            }

            ImageIO.write(bufferedImage, "png", targetPath.toFile() );
            // ---------------------------------------------------------------------

            // Save the generated image ID to the user's profile
            // ---------------------------------------------------------------------
            String avatarUrl = "/static/uploads/avatar/" + generatedName;
            profile.updateAvatar(avatarUrl);
            accountsProfileRepository.save(profile);
            // ---------------------------------------------------------------------

            // Return
            Map<String, Object> response =new LinkedHashMap<>();
            response.put("avatar", avatarUrl);
            return response;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}