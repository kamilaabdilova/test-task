package org.example.testtaskmega.repository;

import org.example.testtaskmega.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
