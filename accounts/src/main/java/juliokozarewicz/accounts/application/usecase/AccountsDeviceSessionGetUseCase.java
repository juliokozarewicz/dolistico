package juliokozarewicz.accounts.application.usecase;

import juliokozarewicz.accounts.application.command.AccountsDeviceSessionGetCommand;
import juliokozarewicz.accounts.application.command.AccountsDeviceSessionsCommand;
import juliokozarewicz.accounts.application.command.AccountsDeviceSessionsResponseCommand;
import juliokozarewicz.accounts.domain.entity.AccountsDeviceSessionEntity;
import juliokozarewicz.accounts.domain.repository.AccountsDeviceSessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AccountsDeviceSessionGetUseCase {

    private final AccountsDeviceSessionRepository accountsDeviceSessionRepository;

    public AccountsDeviceSessionGetUseCase(
        AccountsDeviceSessionRepository accountsDeviceSessionRepository
    ) {
        this.accountsDeviceSessionRepository = accountsDeviceSessionRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> execute(
        UUID idUser,
        AccountsDeviceSessionGetCommand command
    ) {

        int pageNumber = command.pageNumber() != null && command.pageNumber() >= 1
            ? command.pageNumber()
            : 1;

        Pageable pageable = PageRequest.of(
            pageNumber - 1,
            10,
            Sort.by("createdAt").descending()
        );

        Page<AccountsDeviceSessionEntity> page = accountsDeviceSessionRepository
            .findByIdUser(idUser, pageable);

        List<AccountsDeviceSessionsResponseCommand> content = page.getContent()
            .stream()
            .map(entity -> new AccountsDeviceSessionsResponseCommand(
                entity.getId(),
                entity.getCreatedAt(),
                entity.getIpAddress(),
                entity.getLocation(),
                entity.getDevice(),
                entity.getMethod()
            ))
        .toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", content);
        result.put("currentPage", pageNumber);
        result.put("pageSize", 10);
        result.put("totalPages", page.getTotalPages());
        result.put("totalElements", page.getTotalElements());

        return result;

    }

}