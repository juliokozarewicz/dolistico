package juliokozarewicz.tasks.domain.entity;

import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;

import java.time.LocalDateTime;

public class TasksEntity {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    public TasksEntity(

        Integer priority,
        LocalDateTime startTime,
        LocalDateTime endTime


    ) {

        validateBusinessRules(
            priority,
            startTime,
            endTime
        );

    }

    // ===================================================== ( constructor end )

    private void validateBusinessRules (

        Integer priority,
        LocalDateTime startTime,
        LocalDateTime endTime

    ) {

        if (priority == null || priority < 0 || priority > 5) {
            throw new DomainException(DomainExceptionEnum.INVALID_PRIORITY);
        }

        if (startTime == null || endTime == null) {
            throw new DomainException(DomainExceptionEnum.INVALID_DATE);
        }

        if (startTime.isAfter(endTime)) {
            throw new DomainException(DomainExceptionEnum.INVALID_DATE_RANGE);
        }

    }

}
