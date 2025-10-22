package tasks.controllers;

import tasks.dtos.TasksDTO;
import tasks.services.TasksService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
@Validated
class TasksController {

    // Service
    private final TasksService tasksService;

    // constructor
    public TasksController(
        TasksService tasksService
    ) {
        this.tasksService = tasksService;
    }

    @GetMapping("${TASKS_BASE_URL}/tasks")
    public ResponseEntity handle(

        // dtos errors
        @Valid TasksDTO tasksDTO,
        BindingResult bindingResult

    ) {

        // message
        String message = tasksDTO.message() != null ?
            tasksDTO.message() : "Hello World!";

        return tasksService.execute(message);

    }

}