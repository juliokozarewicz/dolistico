package juliokozarewicz.tasks.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    private  UUID idUser;
    private  UUID idCreated;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private  LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private  LocalDateTime updatedAt;

    private  String taskName;
    private  String description;
    private  UUID category;
    private String categoryName;
    private  String color;
    private  Integer priority;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private  LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private  LocalDateTime endTime;

    private  String location;
    private  boolean allDay;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private  LocalDateTime reminderTime;

    private  boolean notifyActive;
    private  String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private  LocalDateTime dueDate;

    public TasksEntity(

        UUID idUser,
        UUID idCreated,
        LocalDateTime createdAt,
        LocalDateTime updatedAtAt,
        String taskName,
        String description,
        UUID category,
        String categoryName,
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

        this.idUser = idUser;
        this.idCreated = idCreated;
        this.createdAt = createdAt;
        this.updatedAt = updatedAtAt;
        this.taskName = taskName;
        this.description = description;
        this.category = category;
        this.categoryName = categoryName;
        this.color = color;
        this.priority = priority;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.allDay = Boolean.TRUE.equals(allDay);
        this.reminderTime = reminderTime;
        this.notifyActive = Boolean.TRUE.equals(notifyActive);
        this.status = status;
        this.dueDate = dueDate;

        validateBusinessRules();

    }

    // ===================================================== ( constructor end )

    private void validateBusinessRules () {

        //------------------------------------------------------- (  user init )

        if (idUser == null) {
            throw new DomainException(DomainExceptionEnum.TASKS_INVALID_CREDENTIALS);
        }

        //-------------------------------------------------------- (  user end )

        //--------------------------------------------- (  priority rules init )

        if ( priority == null || priority < 0 || priority > 5 ) {
            throw new DomainException(DomainExceptionEnum.TASKS_INVALID_PRIORITY);
        }

        //---------------------------------------------- (  priority rules end )

        //------------------------------------------------------- (  date init )

        // StartTime and EndTime validate
        if (
            ( startTime != null && endTime == null ) ||
            ( startTime == null && endTime != null )
        ) {
            throw new DomainException(DomainExceptionEnum.TASKS_INVALID_DATE_RANGE);
        }

        if ( startTime != null && endTime != null ) {

            // Check that startTime and endTime are on the same day
            if ( !startTime.toLocalDate().equals( endTime.toLocalDate() ) ) {
                throw new DomainException(DomainExceptionEnum.TASKS_INVALID_DATE_RANGE);
            }

            // If dueDate exists, startTime and endTime must be on the same day as dueDate
            if (
                dueDate != null &&
                ( !startTime.toLocalDate().equals( dueDate.toLocalDate() ) ||
                !endTime.toLocalDate().equals(dueDate.toLocalDate()))
            ){
                throw new DomainException(DomainExceptionEnum.TASKS_INVALID_DATE_RANGE);
            }

            // Ensure startTime is before endTime
            if (startTime.isAfter(endTime)) {
                throw new DomainException(DomainExceptionEnum.TASKS_INVALID_DATE_RANGE);
            }

        }

        // All day validate
        if (Boolean.TRUE.equals(allDay) && (startTime != null || endTime != null)) {
            throw new DomainException(DomainExceptionEnum.TASKS_INVALID_ALLDAY);
        }

        // Reminder time validate
        if (reminderTime != null && dueDate != null && reminderTime.isAfter(dueDate)) {
            throw new DomainException(DomainExceptionEnum.TASKS_INVALID_REMINDER);
        }

        // Notify / Reminder consistency (XOR)
        if ( (reminderTime != null) ^ notifyActive ) {
            throw new DomainException(DomainExceptionEnum.TASKS_INVALID_REMINDER);
        }

        //-------------------------------------------------------- (  date end )

    }

}