package juliokozarewicz.categories.application.usecase;

import juliokozarewicz.categories.application.command.CategoriesGetCommand;
import juliokozarewicz.categories.application.command.CategoriesGetResponseCommand;
import juliokozarewicz.categories.domain.entity.CategoriesEntity;
import juliokozarewicz.categories.domain.repository.CategoriesRepository;
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
public class CategoriesGetUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final CategoriesRepository categoriesRepository;

    public CategoriesGetUseCase(

        CategoriesRepository categoriesRepository

    ) {

        this.categoriesRepository = categoriesRepository;

    }

    // ===================================================== ( constructor end )

    @Transactional(readOnly = true)
    public Map<String, Object> execute (

        Map<String, Object> credentialsData,
        CategoriesGetCommand categoriesGetCommand

    ) {

        // Credentials
        UUID idUser = UUID.fromString((String) credentialsData.get("id"));

        // --------------------------------------------------- (pagination init)
        int pageNumber = categoriesGetCommand.pageNumber() != null && categoriesGetCommand.pageNumber() >= 1
        ? categoriesGetCommand.pageNumber()
        : 1;

        int pageSize = categoriesGetCommand.sizePagination() != null
        ? categoriesGetCommand.sizePagination()
        : 50;

        Pageable pageable = PageRequest.of(
            pageNumber - 1,
            pageSize,
            Sort.by("createdAt").descending()
        );

        Page<CategoriesEntity> page = categoriesRepository.findAllByIdUser(
            idUser,
            categoriesGetCommand.categoryName(),
            pageable
        );

        List<CategoriesGetResponseCommand> content = page.getContent()
            .stream()
            .map(entity -> new CategoriesGetResponseCommand (
                entity.getIdCreated(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCategoryName()
            ))
            .toList();
        // ---------------------------------------------------- (pagination end)

        // Return map
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", content);
        result.put("currentPage", pageNumber);
        result.put("pageSize", pageSize);
        result.put("totalPages", page.getTotalPages());
        result.put("totalElements", page.getTotalElements());
        return result;

    }

}