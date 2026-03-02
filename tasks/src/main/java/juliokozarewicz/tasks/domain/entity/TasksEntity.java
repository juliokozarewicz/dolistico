package juliokozarewicz.tasks.domain.entity;

import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;

public class TasksEntity {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    public TasksEntity(

        Integer priority

    ) {

        validateBusinessRules(
            priority
        );

    }

    // ===================================================== ( constructor end )

    private void validateBusinessRules (

        Integer priority

    ) {

        if (priority == null) {
            throw new DomainException(DomainExceptionEnum.INVALID_PRIORITY);
        }

        if (priority < 0 || priority > 5) {
            throw new DomainException(DomainExceptionEnum.INVALID_PRIORITY);
        }

    }

}
