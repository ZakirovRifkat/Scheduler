package com.mazyavr.schedule.controller;

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
  public @ResponseBody String addNewProject(@RequestParam String name) {
    projectService.add(name);
    return "Проект добавлен";
  }

  @DeleteMapping(path = "/delete")
  public @ResponseBody String delProject(@RequestParam Long id) {
    projectService.delete(id);
    return "Проект удален";
  }

  @PutMapping(path = "/update")
  public @ResponseBody String updateProject(@RequestParam Long id, @RequestParam String name) {
    projectService.update(id, name);
    return "Проект обновлен";
  }

  @GetMapping(path = "/all")
  public @ResponseBody Iterable<ProjectEntity> getAllProjects() {
    return projectService.getAll();
  }
}
