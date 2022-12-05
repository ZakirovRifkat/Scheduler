package com.mazyavr.schedule.dto;

import com.mazyavr.schedule.entity.TaskEntity;

public class TaskDto {
  
  private Long id;
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
  
  
  public void setId(Long id) {
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
}
