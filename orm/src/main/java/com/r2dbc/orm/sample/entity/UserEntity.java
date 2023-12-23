package com.r2dbc.orm.sample.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;

@Builder
@Table("USER")
public class UserEntity {

  @Id
  public String id;
  public String name;

  public String parentId;
  public String countryId;
  public LocalDateTime createTime;





}
