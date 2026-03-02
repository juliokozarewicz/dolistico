package juliokozarewicz.tasks.infrastructure.persistence.repositoryimpl;

import juliokozarewicz.tasks.domain.repository.TasksRepository;
import java.time.LocalDate;

public class TasksRepositoryImpl implements TasksRepository {

    @Override
    public void existsByNameAndDate(String name, LocalDate date) {}

    @Override
    public void save () {}

}