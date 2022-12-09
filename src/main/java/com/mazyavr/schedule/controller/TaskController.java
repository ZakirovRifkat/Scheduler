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
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @PostMapping(path = "/add")
    public @ResponseBody TaskDto addNewTask(@RequestParam long id, @RequestParam String name,
                                            @RequestParam String description, @RequestParam ZonedDateTime start,
                                            @RequestParam ZonedDateTime end) {
        try {
            var task = taskService.add(id, name, description, start, end);
            return TaskDto.fromEntity(task);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such project");
        }
    }

    @PutMapping(path = "/update")
    public @ResponseBody TaskDto updateTask(@RequestParam long id, @RequestParam String name,
                                               @RequestParam String description, @RequestParam ZonedDateTime start,
                                               @RequestParam ZonedDateTime end,
                                               @RequestParam boolean status, @RequestParam Long priority) {
        try {
            var task = taskService.update(id, name, description, start, end, status, priority);
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
}

