package com.mazyavr.schedule.repository;

import com.mazyavr.schedule.entity.TaskEntity;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<TaskEntity, Long> {
}
