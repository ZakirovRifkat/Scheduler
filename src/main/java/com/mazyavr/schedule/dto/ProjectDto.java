package com.mazyavr.schedule.dto;

import com.mazyavr.schedule.entity.ProjectEntity;

record ProjectDto (
  Long id,
  String name
) {
  public static ProjectDto fromEntity (ProjectEntity entity) {
    return new ProjectDto(entity.getId(), entity.getName());
  }
}

//public class ProjectDto {
//
//  public Long id;
//  public String name;
//
//  public static ProjectDto fromEntity (ProjectEntity entity) {
//    ProjectDto dto = new ProjectDto();
//
//    dto.setId(entity.getId());
//    dto.setName(entity.getName());
//
//    return dto;
//  }
//
//  public void setId(Long id) {
//    this.id = id;
//  }
//
//  public void setName(String name) {
//    this.name = name;
//  }
//
//}
