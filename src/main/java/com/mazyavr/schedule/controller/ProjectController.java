package com.mazyavr.schedule.controller;

import com.mazyavr.schedule.dto.SimpleResponse;
import com.mazyavr.schedule.entity.ProjectEntity;
import com.mazyavr.schedule.service.ProjectService;
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
@RequestMapping(path = "/projects")
public class ProjectController {

  @Autowired
  private ProjectService projectService;

  @PostMapping(path = "/add")
  public @ResponseBody ProjectEntity addNewProject(@RequestParam String name) {
    
    return projectService.add(name);
  }

  @DeleteMapping(path = "/delete")
  public @ResponseBody SimpleResponse delProject(@RequestParam Long id) {
    projectService.delete(id);
    
    return new SimpleResponse();
  }

  @PutMapping(path = "/update")
  public @ResponseBody ProjectEntity updateProject(@RequestParam Long id, @RequestParam String name) {

    
    return projectService.update(id, name);
  }

  @GetMapping(path = "/all")
  public @ResponseBody Iterable<ProjectEntity> getAllProjects() {
    return projectService.getAll();
  }
}
