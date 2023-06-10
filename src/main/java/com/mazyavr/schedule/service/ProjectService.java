package com.mazyavr.schedule.service;

import com.mazyavr.schedule.entity.ProjectEntity;
import com.mazyavr.schedule.entity.UserEntity;
import com.mazyavr.schedule.repository.ProjectRepository;
import com.mazyavr.schedule.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    public Iterable<ProjectEntity> getAll(long userId) {
        List<ProjectEntity> projects = new ArrayList<>();

        for (ProjectEntity t : projectRepository.findAll()) {
            if (t.getUser().getId() == userId) {
                projects.add(t);
            }
        }

        return projects;
    }

    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

    public ProjectEntity add(long userId, String name) {

        ProjectEntity project = new ProjectEntity();
        project.setName(name);

        var userO = userRepository.findById(userId);

        if (userO.isEmpty()) {
            throw new IllegalArgumentException("No such user");
        }

        UserEntity user = userO.get();
        project.setUser(user);

        return projectRepository.save(project);
    }

    public ProjectEntity update(Long id, String name) {

        ProjectEntity project = new ProjectEntity();
        project.setId(id);
        project.setName(name);

        return projectRepository.save(project);
    }
}
