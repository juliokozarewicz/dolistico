package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsCreateCommand;
import juliokozarewicz.accounts.domain.entity.AccountsProfileEntity;
import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import juliokozarewicz.accounts.domain.repository.AccountsProfileRepository;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakCreateUser;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakDeleteUser;
import juliokozarewicz.accounts.infrastructure.keycloak.AccountsKeycloakGetUser;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsAlreadyExistProducer;
import juliokozarewicz.accounts.infrastructure.messaging.producer.AccountsWelcomeProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class AccountsCreateUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AccountsCreateUseCase.class);
    private final AccountsKeycloakCreateUser accountsKeycloakCreateUser;
    private final AccountsKeycloakGetUser accountsKeycloakGetUser;
    private final AccountsKeycloakDeleteUser accountsKeycloakDeleteUser;
    private final AccountsProfileRepository accountsProfileRepository;
    private final AccountsAlreadyExistProducer accountsAlreadyExistProducer;
    private final AccountsWelcomeProducer accountsWelcomeProducer;

    public AccountsCreateUseCase (

        AccountsKeycloakCreateUser accountsKeycloakCreateUser,
        AccountsKeycloakGetUser accountsKeycloakGetUser,
        AccountsKeycloakDeleteUser accountsKeycloakDeleteUser,
        AccountsProfileRepository accountsProfileRepository,
        AccountsAlreadyExistProducer accountsAlreadyExistProducer,
        AccountsWelcomeProducer accountsWelcomeProducer

    ) {

        this.accountsKeycloakCreateUser = accountsKeycloakCreateUser;
        this.accountsKeycloakGetUser = accountsKeycloakGetUser;
        this.accountsKeycloakDeleteUser = accountsKeycloakDeleteUser;
        this.accountsProfileRepository = accountsProfileRepository;
        this.accountsAlreadyExistProducer = accountsAlreadyExistProducer;
        this.accountsWelcomeProducer = accountsWelcomeProducer;

    }

    // ===================================================== ( constructor end )

    public void execute(

        Locale locale,
        AccountsCreateCommand accountsCreateCommand

    ) {

        // Password cleanup
        char[] password = accountsCreateCommand.userPassword();

        // Recent account
        String idUserCreated = null;

        try {

            // Check if user already exists
            Map<String, Object> existingUser = accountsKeycloakGetUser.getUserByEmail(
                accountsCreateCommand.email()
            );

            // Check if user already exists and send email notification
            if (existingUser != null) {
                accountsAlreadyExistProducer.execute(locale, accountsCreateCommand.email());
                return;
            }

            // Create user
            idUserCreated = accountsKeycloakCreateUser.execute(
                accountsCreateCommand.email(),
                password
            );

            // Create user profile table
            Instant timeStamp = Instant.now();

            AccountsProfileEntity profile = new AccountsProfileEntity(
                UUID.fromString(idUserCreated),
                timeStamp,
                timeStamp,
                null,
                accountsCreateCommand.fullName(),
                null,
                null,
                null,
                null,
                null,
                null,
                null
            );

            // Create profile
            accountsProfileRepository.save(profile);

            // Welcome message
            accountsWelcomeProducer.execute(locale, accountsCreateCommand.email());

        }

        // Keycloak conflict error - ignore (409)
        catch (WebClientResponseException.Conflict ignored) { return; }

        // Fallback (500)
        catch (Exception e) {

            // Logs
            logger.atError().log("Create user account error [ AccountsCreateUseCase.execute() ] : ", e);

            // Rollback in a recent account with error
            if (idUserCreated != null) {
                accountsKeycloakDeleteUser.execute(idUserCreated);
            }

            throw new DomainException(DomainExceptionEnum.INTERNAL_INSTABILITY);

        }

        // Cleanup password
        finally {

            if (password != null) {
                java.util.Arrays.fill(password, '\0');
            }

        }

    }

}