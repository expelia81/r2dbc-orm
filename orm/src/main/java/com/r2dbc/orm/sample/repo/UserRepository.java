package com.r2dbc.orm.sample.repo;

import com.r2dbc.orm.sample.entity.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserRepository extends R2dbcRepository<UserEntity, String> {

}
