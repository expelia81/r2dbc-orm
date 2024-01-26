package com.r2dbc.orm.sample.entity;

import com.r2dbc.orm.a_second_draft.annotations.R2dbcManyToOne;
import com.r2dbc.orm.a_second_draft.annotations.R2dbcTable;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Builder
@Table("USER")
@R2dbcTable
public class User {

  @Id
  public String id;
  public String name;

  @R2dbcManyToOne(name = "parent_id", targetColumnName = "id", alias = "parent")
  public User parent;
  public LocalDateTime createTime;

  @R2dbcManyToOne(name = "location_id", targetColumnName = "id")
  public Location location;





}
