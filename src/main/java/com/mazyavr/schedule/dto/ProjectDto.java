package com.mazyavr.schedule.dto;

import com.mazyavr.schedule.entity.ProjectEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Сущность проекта")
public record ProjectDto (
    @Schema(description = "Айди проекта")
  long id,
    @Schema(description = "Имя")
    String name
  
) {
  @Operation(
      summary = "Создаёт проект"
  )
  public static ProjectDto fromEntity (ProjectEntity entity) {
    return new ProjectDto(entity.getId(), entity.getName());
  }
}

/*public class ProjectDto {

  public Long id;
  public String name;

  public static ProjectDto fromEntity (ProjectEntity entity) {
    ProjectDto dto = new ProjectDto();

    dto.setId(entity.getId());
    dto.setName(entity.getName());

    return dto;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

}*/
