package juliokozarewicz.tasks.domain.repository;

import java.time.LocalDate;

public interface TasksRepository {

    void existsByNameAndDate (String name, LocalDate date);
    void save ();

}
