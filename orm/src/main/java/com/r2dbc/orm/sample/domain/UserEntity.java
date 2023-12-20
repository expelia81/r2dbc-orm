package com.r2dbc.orm.sample.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.data.annotation.Id;

@Builder
public class UserEntity {

  @Id
  public String id;
  public String name;

  public String parentId;
  public String countryId;
  public LocalDateTime createTime;





}
