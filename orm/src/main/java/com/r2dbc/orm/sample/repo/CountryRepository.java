package com.r2dbc.orm.sample.repo;

import com.r2dbc.orm.sample.entity.CountryEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Component;

@Component
public interface CountryRepository extends R2dbcRepository<CountryEntity, String> {

}
