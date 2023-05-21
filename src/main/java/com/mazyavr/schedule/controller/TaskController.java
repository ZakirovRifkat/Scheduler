package com.mazyavr.schedule.controller;

import com.mazyavr.schedule.dto.SimpleResponse;
import com.mazyavr.schedule.dto.TaskDto;
import com.mazyavr.schedule.entity.TaskEntity;
import com.mazyavr.schedule.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;

@Tag(name="Таск контроллер", description="Контролер для работы с тасками")
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

  @Operation(
      summary = "Добавление нового таска"
  )
  @PostMapping(path = "/add")
  public @ResponseBody TaskDto addNewTask(@RequestParam long id, @RequestParam String name,
      @RequestParam String description, @RequestParam String start,
      @RequestParam String end, @RequestParam long priority) {
      
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
      var task = taskService.add(id, name, description, start0, end0, priority);
      return TaskDto.fromEntity(task);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such project");
    }
  }

  @Operation(
      summary = "Редактирование таска"
  )
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

  @Operation(
      summary = "Удаление таска"
  )
  @DeleteMapping(path = "/delete")
  public @ResponseBody SimpleResponse deleteTask(@RequestParam long id) {
    taskService.delete(id);

    return new SimpleResponse();
  }

  @Operation(
      summary = "Получить все таски"
  )
  @GetMapping(path = "/all")
  public @ResponseBody Iterable<TaskEntity> getAllTasks(@RequestParam long id) {
    return taskService.getAll(id);
  }

  @Operation(
      summary = "Получить все сегодняшние таски"
  )
  @GetMapping(path = "/today")
  public @ResponseBody Iterable<TaskEntity> getToday() {
    return taskService.getToday();
  }

  @Operation(
      summary = "Получить запланированные таски"
  )
  @GetMapping(path = "/planed")
  public @ResponseBody Iterable<TaskEntity> getPlaned() {
    return taskService.getPlaned();
  }

  @Operation(
      summary = "Получить недоделанные таски"
  )
  @GetMapping(path = "/notdone")
  public @ResponseBody Iterable<TaskEntity> getNotDone() {
    return taskService.getNotDone();
  }
}

