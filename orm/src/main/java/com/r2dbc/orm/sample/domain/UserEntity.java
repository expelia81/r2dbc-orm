package com.r2dbc.orm.sample.domain;

import com.r2dbc.orm.first_draft.annotations.R2dbcTable;
import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.data.annotation.Id;

@Builder
@R2dbcTable("user")
public class UserEntity {

  @Id
  public String id;
  public String name;

  public String parentId;
  public String countryId;
  public LocalDateTime createTime;





}
