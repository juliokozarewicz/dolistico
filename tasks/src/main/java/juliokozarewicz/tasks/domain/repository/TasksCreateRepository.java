package juliokozarewicz.tasks.domain.repository;

import java.time.LocalDate;

public interface TasksCreateRepository {

    void existsByNameAndDate (String name, LocalDate date);
    void save ();

}
