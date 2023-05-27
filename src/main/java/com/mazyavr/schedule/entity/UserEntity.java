package com.mazyavr.schedule.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String googleId;
    private String refreshToken;
//  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
//  private List<ProjectEntity> projects;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

//    public List<ProjectEntity> getProjects() {
//        return projects;
//    }
//
//    public void setProjects(List<ProjectEntity> projects) {
//        this.projects = projects;
//    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
