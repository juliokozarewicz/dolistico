package juliokozarewicz.categories.infrastructure.persistence.specification;

import jakarta.persistence.criteria.Predicate;
import juliokozarewicz.categories.infrastructure.persistence.model.CategoriesModel;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoriesGetSpecification {

    public static Specification<CategoriesModel> filter(

        String categoryName,
        UUID idUser

    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("idUser"), idUser));

            // categoryName
            if (categoryName != null && !categoryName.isBlank()) {
                predicates.add(cb.like(
                    cb.lower(root.get("categoryName")),
                    "%" + categoryName.toLowerCase() + "%"
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));

        };

    }

}