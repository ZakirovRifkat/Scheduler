package com.mazyavr.schedule.repository;

import com.mazyavr.schedule.entity.ProjectEntity;
import org.springframework.data.repository.CrudRepository;

public interface ProjectRepository extends CrudRepository<ProjectEntity, Long> {
}
