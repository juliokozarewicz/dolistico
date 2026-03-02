package juliokozarewicz.tasks.application.usecase;

import juliokozarewicz.tasks.domain.model.TasksCreateModel;
import org.springframework.stereotype.Service;

@Service
public class TasksCreateUseCase {

    public String execute(String message) {

        TasksCreateModel finalMessage = new TasksCreateModel(message);
        return finalMessage.getMessage();

    }

}