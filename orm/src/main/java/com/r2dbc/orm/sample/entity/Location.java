package com.r2dbc.orm.sample.entity;

import com.r2dbc.orm.a_second_draft.annotations.R2dbcTable;
import lombok.Builder;
import org.springframework.data.annotation.Id;

@Builder
@R2dbcTable(name = "LOCATION")
public class Location {

  @Id
  public String id;
  public String name;



}
