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
        LocalDateTime endTime,
        Boolean allDay,
        LocalDateTime reminderTime,
        LocalDateTime dueDate,
        String taskName


    ) {

        validateBusinessRules(
            priority,
            startTime,
            endTime,
            allDay,
            reminderTime,
            dueDate,
            taskName
        );

    }

    // ===================================================== ( constructor end )

    private void validateBusinessRules (

        Integer priority,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Boolean allDay,
        LocalDateTime reminderTime,
        LocalDateTime dueDate,
        String taskName

    ) {

        //--------------------------------------------- (  priority rules INIT )

        if ( priority == null || priority < 0 || priority > 5 ) {
            throw new DomainException(DomainExceptionEnum.INVALID_PRIORITY);
        }

        //---------------------------------------------- (  priority rules END )

        //------------------------------------------------------ (  dates INIT )

        if (startTime != null && endTime != null) {

            if (!startTime.toLocalDate().equals(endTime.toLocalDate())) {
                throw new DomainException(DomainExceptionEnum.INVALID_DATE_RANGE);
            }

            if (dueDate != null && !startTime.toLocalDate().equals(dueDate.toLocalDate())) {
                throw new DomainException(DomainExceptionEnum.INVALID_DATE_RANGE);
            }

            if (startTime.isAfter(endTime)) {
                throw new DomainException(DomainExceptionEnum.INVALID_DATE_RANGE);
            }

        }

        if (Boolean.TRUE.equals(allDay) && (startTime != null || endTime != null)) {
            throw new DomainException(DomainExceptionEnum.INVALID_ALLDAY);
        }

        if (reminderTime != null && dueDate != null && reminderTime.isAfter(dueDate)) {
            throw new DomainException(DomainExceptionEnum.INVALID_REMINDER);
        }

        //------------------------------------------------------- (  dates END )

    }

}