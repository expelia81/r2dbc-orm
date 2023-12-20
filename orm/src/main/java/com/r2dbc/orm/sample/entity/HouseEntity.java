package com.r2dbc.orm.sample.entity;

import org.springframework.data.annotation.Id;

public class HouseEntity {

  @Id
  private String id;

  private String name;

  private String location;

  private String countryId;

  private String ownerId;

}
