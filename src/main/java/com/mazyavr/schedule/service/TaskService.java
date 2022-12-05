package com.mazyavr.schedule.service;

import com.mazyavr.schedule.dto.TaskDto;
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

  public TaskEntity add(Long projectId, String name, String description, ZonedDateTime time) {
    TaskEntity task = new TaskEntity();
    
    task.setDescription(description);
    task.setName(name);
    task.setTime(time);
    task.setStatus(false);
    
    ProjectEntity project = projectRepository.findById(projectId).get();
    task.setProject(project);
    
    return (taskRepository.save(task));
  }

  public TaskEntity update(Long id, String name, String description, ZonedDateTime time,
      boolean status, Long priority) {
    TaskEntity task = taskRepository.findById(id).get();
    
    task.setName(name);
    task.setDescription(description);
    task.setTime(time);
    task.setStatus(status);
    task.setPriority(priority);
    
    return taskRepository.save(task);
  }

  public Iterable<TaskEntity> getAll(Long projectId) {
    List<Long> ids = new ArrayList<>();
    
    for (TaskEntity t : taskRepository.findAll()) {
      if (t.getProject().getId().equals(projectId)) {
        ids.add(t.getId());
      }
    }
    
    return taskRepository.findAllById(ids);
  }
}
