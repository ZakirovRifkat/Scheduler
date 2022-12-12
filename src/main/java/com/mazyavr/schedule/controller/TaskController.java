package com.mazyavr.schedule.controller;

import com.mazyavr.schedule.dto.SimpleResponse;
import com.mazyavr.schedule.dto.TaskDto;
import com.mazyavr.schedule.entity.TaskEntity;
import com.mazyavr.schedule.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;

@Controller
@RequestMapping("/tasks")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class TaskController {

  @Autowired
  private TaskService taskService;

  /**
   * временная зона "+01:00" - Париж, "+03:00" - Москва(вроде) "+" = "%2B" "-" = "-"
   *
   * @param start - format "2011-12-03T10:15:30+01:00" "год-месяц-день T час:минута:секунда+зона"
   * @param end   - format "2011-12-03T10:15:30+01:00"
   */
  
  @PostMapping(path = "/add")
  public @ResponseBody TaskDto addNewTask(@RequestParam long id, @RequestParam String name,
      @RequestParam String description, @RequestParam String start,
      @RequestParam String end) {
      
    ZonedDateTime start0;
    ZonedDateTime end0;
    
    try {
      start0 = ZonedDateTime.parse(start);
      end0 = ZonedDateTime.parse(end);
      
      if (start0.compareTo(end0) > 0) {
        throw new IllegalArgumentException();
      }
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong Date");
    }
    
    try {
      var task = taskService.add(id, name, description, start0, end0);
      return TaskDto.fromEntity(task);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such project");
    }
  }

  @PutMapping(path = "/update")
  public @ResponseBody TaskDto updateTask(@RequestParam long id, @RequestParam String name,
      @RequestParam String description, @RequestParam String start,
      @RequestParam String end,
      @RequestParam boolean status, @RequestParam Long priority) {
      
    ZonedDateTime start0;
    ZonedDateTime end0;
    
    try {
      start0 = ZonedDateTime.parse(start);
      end0 = ZonedDateTime.parse(end);
      
      if (start0.compareTo(end0) > 0) {
        throw new IllegalArgumentException();
      }
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong Date");
    }
    
    try {
      var task = taskService.update(id, name, description, start0, end0, status, priority);
      return TaskDto.fromEntity(task);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such project");
    }
  }

  @DeleteMapping(path = "/delete")
  public @ResponseBody SimpleResponse deleteTask(@RequestParam long id) {
    taskService.delete(id);

    return new SimpleResponse();
  }

  @GetMapping(path = "/all")
  public @ResponseBody Iterable<TaskEntity> getAllTasks(@RequestParam long id) {
    return taskService.getAll(id);
  }
  
  @GetMapping(path = "/today")
  public @ResponseBody Iterable<TaskEntity> getToday() {
    return taskService.getToday();
  }
  
  @GetMapping(path = "/planed")
  public @ResponseBody Iterable<TaskEntity> getPlaned() {
    return taskService.getPlaned();
  }
  
  @GetMapping(path = "/notdone")
  public @ResponseBody Iterable<TaskEntity> getNotDone() {
    return taskService.getNotDone();
  }
}

