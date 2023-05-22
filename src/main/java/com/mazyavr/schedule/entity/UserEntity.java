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
  
  private long email;
  private String token;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  private List<ProjectEntity> projects;
  
  public long getId() { return id; }
  
  public void setId(long id) {
    this.id = id;
  }
  
  public long getEmail() {return email; }
  
  public void setEmail(long email) { this.email = email; }

  public List<ProjectEntity> getProjects() {
    return projects;
  }

  public void setProjects(List<ProjectEntity> projects) {
    this.projects = projects;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
