package com.mazyavr.schedule.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class ProjectEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  
  @OneToMany (cascade = CascadeType.ALL, mappedBy = "project")
  private List<TaskEntity> tasks;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }
}
