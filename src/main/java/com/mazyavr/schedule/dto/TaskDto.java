package com.mazyavr.schedule.dto;

import com.mazyavr.schedule.entity.TaskEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;

@Schema(description = "Сущность события (таска)")
public record TaskDto (
    @Schema(description = "Айди события")
  long id,
    @Schema(description = "Имя события")
  String name,
    @Schema(description = "Описание события")
  String description,
    @Schema(description = "Приоритет события")
  Long priority,
    @Schema(description = "Статус выполненности")
  boolean status,
    @Schema(description = "Время начала события")
  ZonedDateTime start,
    @Schema(description = "Время окончания события")
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
