package juliokozarewicz.tasks.application.usecase;

import juliokozarewicz.tasks.application.command.TasksCreateCommand;
import juliokozarewicz.tasks.domain.model.TasksCreateModel;
import org.springframework.stereotype.Service;

@Service
public class TasksCreateUseCase {

    public String execute(TasksCreateCommand tasksCreateCommand) {

        TasksCreateModel approvedRules = new TasksCreateModel(tasksCreateCommand.taskName());
        return approvedRules.getMessage();

    }

}