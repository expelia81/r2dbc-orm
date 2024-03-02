package com.r2dbc.orm.sample.entity;

import com.r2dbc.orm.a_second_draft.annotations.R2dbcManyToOne;
import com.r2dbc.orm.a_second_draft.annotations.R2dbcTable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
@AllArgsConstructor
@R2dbcTable(name = "HOUSE")
@ToString
public class House {

  @Id
  private String id;

  private String name;

  @R2dbcManyToOne(name = "locationId", targetColumnName = "id")
  private Location location;

//  @R2dbcManyToOne(name = "country_id", targetColumnName = "id")
//  private String countryId;

  @R2dbcManyToOne(name = "ownerId", targetColumnName = "id")
  private User owner;

}
