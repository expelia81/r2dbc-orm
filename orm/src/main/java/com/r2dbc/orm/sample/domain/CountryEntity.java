package com.r2dbc.orm.sample.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;

@Builder
@Getter
public class CountryEntity {

  @Id
  public String id;
  public String name;



}
