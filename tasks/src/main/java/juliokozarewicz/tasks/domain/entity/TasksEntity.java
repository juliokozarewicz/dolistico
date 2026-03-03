package juliokozarewicz.tasks.domain.entity;

import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;

import java.time.LocalDateTime;
import java.util.UUID;

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

        //------------------------------------------------------- (  date INIT )

        // StartTime and EndTime validate
        if (
            ( startTime != null && endTime == null ) ||
            ( startTime == null && endTime != null )
        ) {
            throw new DomainException(DomainExceptionEnum.INVALID_DATE_RANGE);
        }

        if ( startTime != null && endTime != null ) {

            // Check that startTime and endTime are on the same day
            if ( !startTime.toLocalDate().equals( endTime.toLocalDate() ) ) {
                throw new DomainException(DomainExceptionEnum.INVALID_DATE_RANGE);
            }

            // If dueDate exists, startTime and endTime must be on the same day as dueDate
            if (
                dueDate != null &&
                ( !startTime.toLocalDate().equals( dueDate.toLocalDate() ) ||
                !endTime.toLocalDate().equals(dueDate.toLocalDate()))
            ){
                throw new DomainException(DomainExceptionEnum.INVALID_DATE_RANGE);
            }

            // Ensure startTime is before endTime
            if (startTime.isAfter(endTime)) {
                throw new DomainException(DomainExceptionEnum.INVALID_DATE_RANGE);
            }

        }

        // All day validate
        if (Boolean.TRUE.equals(allDay) && (startTime != null || endTime != null)) {
            throw new DomainException(DomainExceptionEnum.INVALID_ALLDAY);
        }

        // Reminder time validate
        if (reminderTime != null && dueDate != null && reminderTime.isAfter(dueDate)) {
            throw new DomainException(DomainExceptionEnum.INVALID_REMINDER);
        }

        //-------------------------------------------------------- (  date END )

    }

}