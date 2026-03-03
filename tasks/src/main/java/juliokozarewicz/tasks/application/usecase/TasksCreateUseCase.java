package juliokozarewicz.tasks.application.usecase;

import juliokozarewicz.tasks.application.input.TasksCreateInput;
import juliokozarewicz.tasks.domain.entity.TasksEntity;
import org.springframework.stereotype.Service;

@Service
public class TasksCreateUseCase {

    public void execute(

        TasksCreateInput tasksCreateInput

    ) {

        new TasksEntity(
            tasksCreateInput.priority(),
            tasksCreateInput.startTime(),
            tasksCreateInput.endTime(),
            tasksCreateInput.allDay(),
            tasksCreateInput.reminderTime(),
            tasksCreateInput.dueDate(),
            tasksCreateInput.taskName()
        );

    }

}