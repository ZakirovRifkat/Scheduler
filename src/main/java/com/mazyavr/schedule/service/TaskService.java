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

  public TaskEntity add(Long projectId, String name, String description, ZonedDateTime time) {
    TaskEntity t = new TaskEntity();
    t.setDescription(description);
    t.setName(name);
    t.setTime(time);
    t.setStatus(false);
    ProjectEntity project = projectRepository.findById(projectId).get();
    t.setProject(project);
    return taskRepository.save(t);
  }

  public TaskEntity update(Long id, String name, String description, ZonedDateTime time,
      boolean status, Long priority) {
    TaskEntity t = taskRepository.findById(id).get();
    t.setName(name);
    t.setDescription(description);
    t.setTime(time);
    t.setStatus(status);
    t.setPriority(priority);
    return taskRepository.save(t);
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
