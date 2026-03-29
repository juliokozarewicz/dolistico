package juliokozarewicz.tasks.infrastructure.persistence.specification;

import jakarta.persistence.criteria.Predicate;
import juliokozarewicz.tasks.infrastructure.persistence.model.TasksModel;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TasksGetUserSpecification {

    public static Specification<TasksModel> filter(

        String taskName,
        String category,
        Integer priority,
        String location,
        String status,
        LocalDate dueDateInit,
        LocalDate dueDateEnd,
        UUID idUser

    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("idUser"), idUser));

            // taskName
            if (taskName != null && !taskName.isBlank()) {
                predicates.add(cb.like(
                    cb.lower(root.get("taskName")),
                    "%" + taskName.toLowerCase() + "%"
                ));
            }

            // category
            if (category != null && !category.isBlank()) {
                predicates.add(cb.like(
                    cb.lower(root.join("category").get("categoryName")),
                    "%" + category.toLowerCase() + "%"
                ));
            }

            // priority
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }

            // location
            if (location != null && !location.isBlank()) {
                predicates.add(cb.like(
                    cb.lower(root.get("location")),
                    "%" + location.toLowerCase() + "%"
                ));
            }

            // status
            if (status != null && !status.isBlank()) {
                predicates.add(cb.like(
                    cb.lower(root.get("status")),
                    "%" + status.toLowerCase() + "%"
                ));
            }

            // dueDateInit
            if (dueDateInit != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                    root.get("dueDate"), dueDateInit.atStartOfDay()
                ));
            }

            // dueDateEnd
            if (dueDateEnd != null) {
                predicates.add(cb.lessThanOrEqualTo(
                    root.get("dueDate"), dueDateEnd.atTime(23, 59, 59)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));

        };

    }

}