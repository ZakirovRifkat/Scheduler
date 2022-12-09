package com.mazyavr.schedule.service;

import com.mazyavr.schedule.entity.ProjectEntity;
import com.mazyavr.schedule.entity.TaskEntity;
import com.mazyavr.schedule.repository.ProjectRepository;
import com.mazyavr.schedule.repository.TaskRepository;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

  @Autowired
  private TaskRepository taskRepository;
  @Autowired
  private ProjectRepository projectRepository;

  public void delete(Long id) {
    taskRepository.deleteById(id);
  }

  public TaskEntity add(Long projectId, String name, String description, ZonedDateTime start, ZonedDateTime end) {
    
    TaskEntity task = new TaskEntity();
    
    task.setDescription(description);
    task.setName(name);
    task.setStart(start);
    task.setEnd(end);
    task.setStatus(false);
    
    var projectO = projectRepository.findById(projectId);
    
    if(projectO.isEmpty()) {
      throw new IllegalArgumentException("No such project");
    }
    
    ProjectEntity project = projectO.get();
    task.setProject(project);
    
    return taskRepository.save(task);
  }

  public TaskEntity update(long id, String name, String description, ZonedDateTime start, ZonedDateTime end,
      boolean status, long priority) {
    
    var projectO = taskRepository.findById(id);
  
    if(projectO.isEmpty()) {
      throw new IllegalArgumentException("No such project");
    }
    
    TaskEntity task = projectO.get();
    
    task.setName(name);
    task.setDescription(description);
    task.setStart(start);
    task.setEnd(end);
    task.setStatus(status);
    task.setPriority(priority);
    
    return taskRepository.save(task);
  }

  public Iterable<TaskEntity> getAll(long projectId) {
    
    List<Long> ids = new ArrayList<>();
    
    for (TaskEntity t : taskRepository.findAll()) {
      if (t.getProject().getId() == projectId) {
        ids.add(t.getId());
      }
    }
    
    return taskRepository.findAllById(ids);
  }
}
