package com.mazyavr.schedule.controller;

import com.mazyavr.schedule.dto.ProjectDto;
import com.mazyavr.schedule.dto.SimpleResponse;
import com.mazyavr.schedule.entity.ProjectEntity;
import com.mazyavr.schedule.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Tag(name="Контроллер проектов", description="Контроллер для работы с проектами: добавлением, удалением, редактированием, удалением всех")
@Controller
@RequestMapping(path = "/projects")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ProjectController {

  @Autowired
  private ProjectService projectService;

  @Operation(
      summary = "Добавление нового проекта"
  )
  @PostMapping(path = "/add")
  public @ResponseBody ProjectDto addNewProject(@RequestParam String name) {
    return ProjectDto.fromEntity(projectService.add(name));
  }

  @Operation(
      summary = "Удаление проекта"
  )
  @DeleteMapping(path = "/delete")
  public @ResponseBody SimpleResponse delProject(@RequestParam Long id) {
    projectService.delete(id);
    
    return new SimpleResponse();
  }

  @Operation(
      summary = "Редактирование проекта"
  )
  @PutMapping(path = "/update")
  public @ResponseBody ProjectDto updateProject(@RequestParam Long id, @RequestParam String name) {
    return ProjectDto.fromEntity(projectService.update(id, name));
  }

  @Operation(
      summary = "Удаление всех проектов"
  )
  @GetMapping(path = "/all")
  public @ResponseBody Iterable<ProjectEntity> getAllProjects() {
    return projectService.getAll();
  }
}
