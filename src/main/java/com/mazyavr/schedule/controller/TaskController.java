package com.mazyavr.schedule.controller;

import com.mazyavr.schedule.dto.SimpleResponse;
import com.mazyavr.schedule.dto.TaskDto;
import com.mazyavr.schedule.entity.TaskEntity;
import com.mazyavr.schedule.service.TaskService;
import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private TaskService taskService;

  @PostMapping(path = "/add")
  public @ResponseBody TaskDto addNewTask(@RequestParam Long projectId, @RequestParam String name,
                                          @RequestParam String description, @RequestParam ZonedDateTime time) {
    taskService.add(projectId, name, description, time);
    
    var task = taskService.add(projectId, name, description, time);
    return TaskDto.fromEntity(task);
  }

  @PutMapping(path = "/update")
  public @ResponseBody TaskEntity updateTask(@RequestParam Long id, @RequestParam String name,
      @RequestParam String description, @RequestParam ZonedDateTime time,
      @RequestParam boolean status, @RequestParam Long priority) {
    
    return taskService.update(id, name, description, time, status, priority);
  }

  @DeleteMapping(path = "/delete")
  public @ResponseBody SimpleResponse deleteTask(@RequestParam Long id) {
    taskService.delete(id);
    
    return new SimpleResponse();
  }

  @GetMapping(path = "/all")
  public @ResponseBody Iterable<TaskEntity> getAllTasks(@RequestParam Long projectId) {
    return taskService.getAll(projectId);
  }
}

