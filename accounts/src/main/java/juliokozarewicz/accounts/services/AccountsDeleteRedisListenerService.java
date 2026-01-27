package juliokozarewicz.accounts.services;

import juliokozarewicz.accounts.persistence.entities.AccountsDeletedEntity;
import juliokozarewicz.accounts.persistence.entities.AccountsEntity;
import juliokozarewicz.accounts.persistence.entities.AccountsProfileEntity;
import juliokozarewicz.accounts.persistence.repositories.AccountsDeletedRepository;
import juliokozarewicz.accounts.persistence.repositories.AccountsProfileRepository;
import juliokozarewicz.accounts.persistence.repositories.AccountsRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class AccountsDeleteRedisListenerService implements MessageListener {

    // ==================================================== ( constructor init )

    private final AccountsRepository accountsRepository;
    private final AccountsProfileRepository accountsProfileRepository;
    private final AccountsManagementService accountsManagementService;
    private final EncryptionService encryptionService;
    private final AccountsDeletedRepository accountsDeletedRepository;

    public AccountsDeleteRedisListenerService (

        AccountsRepository accountsRepository,
        AccountsProfileRepository accountsProfileRepository,
        AccountsManagementService accountsManagementService,
        EncryptionService encryptionService,
        AccountsDeletedRepository accountsDeletedRepository

    ) {

        this.accountsRepository = accountsRepository;
        this.accountsProfileRepository = accountsProfileRepository;
        this.accountsManagementService = accountsManagementService;
        this.encryptionService = encryptionService;
        this.accountsDeletedRepository = accountsDeletedRepository;

    }

    // ===================================================== ( constructor end )

    // Delete account (user decision)
    @Override
    @Transactional
    public void onMessage(Message message, byte[] pattern) {

        String expiredKey  = new String(message.getBody());
        UUID idUser = UUID.fromString(expiredKey.substring(expiredKey.indexOf("::") + 2));

        if (expiredKey.startsWith("accounts-deletedAccountByUserCache")) {

            // Get user account
            Optional<AccountsEntity> findUser =  accountsRepository.findById(
                idUser
            );

            // Find the user
            AccountsEntity user = accountsRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException() );

            if (findUser.isPresent()) {

                // Create id
                UUID idCreated = accountsManagementService.createUniqueId();

                // Create deleted account (id user for id and email)
                AccountsDeletedEntity newDeletedAccount = new AccountsDeletedEntity();
                newDeletedAccount.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
                newDeletedAccount.setId(idCreated);
                newDeletedAccount.setEmail(findUser.get().getEmail());
                newDeletedAccount.setUser(user);
                accountsDeletedRepository.save(newDeletedAccount);

                // Change email for user id
                findUser.get().setEmail("deleted-" + idCreated);
                findUser.get().setPassword(
                    encryptionService.hashPassword(
                        accountsManagementService.createUniqueId().toString()
                    )
                );
                accountsRepository.save(findUser.get());

            }

        }

        // Delete account not activated
        if (expiredKey.startsWith("accounts-notActivatedAccountCache")) {

            // Get user account
            Optional<AccountsEntity> findUser =  accountsRepository.findById(
                idUser
            );

            // Get user profile
            Optional<AccountsProfileEntity> findProfile = accountsProfileRepository
                .findById(idUser);

            if ( findUser.isPresent() && findProfile.isPresent() ) {

                // Delete both
                accountsRepository.delete(findUser.get());
                accountsProfileRepository.delete(findProfile.get());

            }

        }

    }

}