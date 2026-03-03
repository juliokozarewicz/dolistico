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
        LocalDateTime dueDate


    ) {

        validateBusinessRules(
            priority,
            startTime,
            endTime,
            allDay,
            reminderTime,
            dueDate
        );

    }

    // ===================================================== ( constructor end )

    private void validateBusinessRules (

        Integer priority,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Boolean allDay,
        LocalDateTime reminderTime,
        LocalDateTime dueDate

    ) {

        //--------------------------------------------- (  priority rules INIT )

        if ( priority == null || priority < 0 || priority > 5 ) {
            throw new DomainException(DomainExceptionEnum.INVALID_PRIORITY);
        }

        //---------------------------------------------- (  priority rules END )

        //------------------------------------------------------ (  dates INIT )

        if (
            ( startTime == null && endTime != null ) ||
            ( startTime != null && endTime == null )
        ) {
            throw new DomainException(DomainExceptionEnum.INVALID_DATE_RANGE);
        }

        if ( startTime != null && startTime.isAfter(endTime) ) {
            throw new DomainException(DomainExceptionEnum.INVALID_DATE_RANGE);
        }

        if ( (allDay == true) && ( startTime != null || endTime != null ) ) {
            throw new DomainException(DomainExceptionEnum.INVALID_ALLDAY);
        }

        if ( reminderTime.isAfter(dueDate) ) {
            throw new DomainException(DomainExceptionEnum.INVALID_REMINDER);
        }

        //------------------------------------------------------- (  dates END )

    }

}