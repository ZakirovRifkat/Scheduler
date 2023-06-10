package com.mazyavr.schedule.controller;

import com.mazyavr.schedule.dto.ProjectDto;
import com.mazyavr.schedule.dto.SimpleResponse;
import com.mazyavr.schedule.entity.ProjectEntity;
import com.mazyavr.schedule.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public @ResponseBody ProjectDto addNewProject(@RequestParam Long userId, @RequestParam String name) {
        return ProjectDto.fromEntity(projectService.add(userId, name));
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
            summary = "Получение списка всех проектов"
    )
    @GetMapping(path = "/all")
    public @ResponseBody Iterable<ProjectEntity> getAllProjects(@RequestParam long userId) {
        return projectService.getAll(userId);
    }
}
