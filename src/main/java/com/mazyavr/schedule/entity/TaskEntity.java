package com.mazyavr.schedule.entity;

import java.time.ZonedDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class TaskEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  private String description;
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long priority;
  private boolean status;
  
  /**
   * A date-time with a time-zone in the ISO-8601 calendar system, such as 2007-12-03T10:15:30+01:00
   * Europe/Paris.
   */
  private ZonedDateTime start;
  private ZonedDateTime end;
  
  @ManyToOne
  @JoinColumn(name = "project_id")
  private ProjectEntity project;

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public boolean isStatus() {
    return status;
  }

  public ZonedDateTime getStart() {
    return start;
  }
  
  public ZonedDateTime getEnd() {
    return end;
  }

  public ProjectEntity getProject() {
    return project;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public void setStart(ZonedDateTime start) {
    this.start = start;
  }
  
  public void setEnd(ZonedDateTime end) {
    this.end = end;
  }

  public void setProject(ProjectEntity project) {
    this.project = project;
  }

  public String getDescription() {
    return description;
  }

  public long getPriority() {
    return priority;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setPriority(Long priority) {
    this.priority = priority;
  }
}
