package com.mazyavr.schedule.repository;

import com.mazyavr.schedule.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

}
