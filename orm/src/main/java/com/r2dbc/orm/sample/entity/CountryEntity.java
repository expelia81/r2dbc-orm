package com.r2dbc.orm.sample.entity;

import lombok.Builder;
import org.springframework.data.annotation.Id;

@Builder
public class CountryEntity {

  @Id
  public String id;
  public String name;



}
