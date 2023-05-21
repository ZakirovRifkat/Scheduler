package com.mazyavr.schedule.dto;

import com.mazyavr.schedule.entity.TaskEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;

public record TaskDto (
  long id,
  String name,
  String description,
  Long priority,
  boolean status,
  ZonedDateTime start,
  ZonedDateTime end
) {
  @Operation(
      summary = "Создание задачи"
  )
  public static TaskDto fromEntity (TaskEntity entity) {
    return new TaskDto(entity.getId(), entity.getName(), entity.getDescription(), entity.getPriority(),
      entity.isStatus(), entity.getStart(), entity.getEnd());
  }
}

/*public class TaskDto {
  
  private long id;
  private String name;
  private String description;
  private Long priority;
  private boolean status;
  
  public static TaskDto fromEntity(TaskEntity entity) {
    TaskDto dto = new TaskDto();
    
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setDescription(entity.getDescription());
    dto.setPriority(entity.getPriority());
    
    return dto;
  }
  
  
  public void setId(long id) {
    this.id = id;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public void setPriority(Long priority) {
    this.priority = priority;
  }
  
  public void setStatus(boolean status) {
    this.status = status;
  }
}*/
