package juliokozarewicz.tasks.application.usecase;

import juliokozarewicz.tasks.domain.exception.DomainException;
import juliokozarewicz.tasks.domain.exception.DomainExceptionEnum;
import juliokozarewicz.tasks.domain.repository.TasksRepository;
import juliokozarewicz.tasks.infrastructure.messaging.producer.TasksEventProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.UUID;

@Service
public class TasksDeleteUseCase {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final TasksRepository tasksRepository;
    private final TasksEventProducer tasksEventProducer;

    public TasksDeleteUseCase(

        TasksRepository tasksRepository,
        TasksEventProducer tasksEventProducer

    ) {

        this.tasksRepository = tasksRepository;
        this.tasksEventProducer = tasksEventProducer;

    }

    // ===================================================== ( constructor end )

    @Transactional
    public void execute(

        UUID idUser,
        String idTask

    ) {

        UUID idDelete = UUID.fromString(idTask);

        // Find existing task (ensures ownership)
        var tasksEntity = tasksRepository.findByIdAndUser(idDelete, idUser)
        .orElseThrow(() -> new DomainException(DomainExceptionEnum.TASKS_NOT_FOUND));

        // Publish delete event to Kafka
        tasksEventProducer.producerDelete(tasksEntity);

    }

}