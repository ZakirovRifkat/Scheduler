package com.mazyavr.schedule.controller;

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
  public @ResponseBody String addNewTask(@RequestParam Long projectId, @RequestParam String name,
      @RequestParam String description, @RequestParam ZonedDateTime time) {
    taskService.add(projectId, name, description, time);
    return "Задача добавлена";
  }

  @PutMapping(path = "/update")
  public @ResponseBody String updateTask(@RequestParam Long id, @RequestParam String name,
      @RequestParam String description, @RequestParam ZonedDateTime time,
      @RequestParam boolean status, @RequestParam Long priority) {
    taskService.update(id, name, description, time, status, priority);
    return "Задача обновлена";
  }

  @DeleteMapping(path = "/delete")
  public @ResponseBody String deleteTask(@RequestParam Long id) {
    taskService.delete(id);
    return "Задача удалена";
  }

  @GetMapping(path = "/all")
  public @ResponseBody Iterable<TaskEntity> getAllTasks(@RequestParam Long projectId) {
    return taskService.getAll(projectId);
  }
}
