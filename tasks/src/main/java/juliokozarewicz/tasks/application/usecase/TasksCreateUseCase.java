package juliokozarewicz.tasks.application.usecase;

import juliokozarewicz.tasks.application.input.TasksCreateInput;
import juliokozarewicz.tasks.domain.entity.TasksEntity;
import org.springframework.stereotype.Service;

@Service
public class TasksCreateUseCase {

    public String execute(

        TasksCreateInput tasksCreateInput

    ) {

        TasksEntity approvedRules = new TasksEntity(tasksCreateInput.taskName());
        return approvedRules.getMessage();

    }

}