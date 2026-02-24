package juliokozarewicz.tasks.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import juliokozarewicz.tasks.dtos.TasksCreateDTO;
import juliokozarewicz.tasks.services.TasksCreateService;

@RestController
@RequestMapping()
@Validated
class TasksController {

    // Service
    private final TasksCreateService tasksCreateService;

    // constructor
    public TasksController(
        TasksCreateService tasksCreateService
    ) {
        this.tasksCreateService = tasksCreateService;
    }

    @PostMapping("/${TASKS_BASE_URL}/create")
    public ResponseEntity handle(

        // dtos errors
        @Valid TasksCreateDTO tasksCreateDTO,
        BindingResult bindingResult

    ) {

        // message
        String message = tasksCreateDTO.message() != null ?
            tasksCreateDTO.message() : "Hello World!";

        return tasksCreateService.execute(message);

    }

}