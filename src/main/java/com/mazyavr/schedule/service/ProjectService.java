package com.mazyavr.schedule.service;

import com.mazyavr.schedule.entity.ProjectEntity;
import com.mazyavr.schedule.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

  @Autowired
  private ProjectRepository projectRepository;

  public Iterable<ProjectEntity> getAll() {
    return projectRepository.findAll();
  }

  public void delete(Long id) {
    projectRepository.deleteById(id);
  }

  public ProjectEntity add(String name) {
    ProjectEntity p = new ProjectEntity();
    p.setName(name);
    return projectRepository.save(p);
  }

  public ProjectEntity update(Long id, String name) {
    ProjectEntity project = new ProjectEntity();
    project.setId(id);
    project.setName(name);
    return projectRepository.save(project);
  }
}
