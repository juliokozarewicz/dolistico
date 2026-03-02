package juliokozarewicz.tasks.infrastructure.persistence.repositoryimpl;

import juliokozarewicz.tasks.domain.repository.TasksCreateRepository;

import java.time.LocalDate;

public class TasksRepositoryImpl implements TasksCreateRepository {

    @Override
    public void existsByNameAndDate(String name, LocalDate date) {}

    @Override
    public void save () {}

}