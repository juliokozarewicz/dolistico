package juliokozarewicz.tasks.domain.entity;

import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class TasksEntity {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private  UUID idCreated;
    private  LocalDateTime createdAt;
    private  LocalDateTime updatedAt;
    private  String taskName;
    private  String description;
    private  String category;
    private  String color;
    private  Integer priority;
    private  LocalDateTime startTime;
    private  LocalDateTime endTime;
    private  String location;
    private  boolean allDay;
    private  LocalDateTime reminderTime;
    private  boolean notifyActive;
    private  String status;
    private  LocalDateTime dueDate;

    public TasksEntity(

        UUID idCreated,
        LocalDateTime createdAt,
        LocalDateTime updatedAtAt,
        String taskName,
        String description,
        String category,
        String color,
        Integer priority,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String location,
        Boolean allDay,
        LocalDateTime reminderTime,
        Boolean notifyActive,
        String status,
        LocalDateTime dueDate

    ) {

        this.idCreated = idCreated;
        this.createdAt = createdAt;
        this.updatedAt = updatedAtAt;
        this.taskName = taskName;
        this.description = description;
        this.category = category;
        this.color = color;
        this.priority = priority;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.allDay = allDay;
        this.reminderTime = reminderTime;
        this.notifyActive = notifyActive;
        this.status = status;
        this.dueDate = dueDate;

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