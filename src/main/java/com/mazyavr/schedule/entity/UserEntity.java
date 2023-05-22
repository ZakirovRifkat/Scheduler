package com.mazyavr.schedule.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  private String email;
  private String refreshToken;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  private List<ProjectEntity> projects;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public List<ProjectEntity> getProjects() {
    return projects;
  }

  public void setProjects(List<ProjectEntity> projects) {
    this.projects = projects;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
