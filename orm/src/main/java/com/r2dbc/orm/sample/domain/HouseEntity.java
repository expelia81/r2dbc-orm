package com.r2dbc.orm.sample.domain;

import com.r2dbc.orm.first_draft.annotations.R2dbcJoinColumn;
import lombok.Builder;
import lombok.Getter;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.data.annotation.Id;

@Getter
@Builder
public class HouseEntity {

  @Id
  private String id;

  private String name;

  private String location;

  private String countryId;

  @R2dbcJoinColumn(name = "")
  private User ownerId;

}
