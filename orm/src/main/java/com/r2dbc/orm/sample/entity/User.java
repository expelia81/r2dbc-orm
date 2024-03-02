package com.r2dbc.orm.sample.entity;

import com.r2dbc.orm.a_second_draft.annotations.R2dbcManyToOne;
import com.r2dbc.orm.a_second_draft.annotations.R2dbcOneToMany;
import com.r2dbc.orm.a_second_draft.annotations.R2dbcTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Table("USER")
@R2dbcTable(name = "USER", alias = "user")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

  @Id
  public String id;
  public String name;

  @R2dbcManyToOne(name = "parent_id", targetColumnName = "id", alias = "parent")
  public User parent;
  public LocalDateTime createTime;

  @R2dbcManyToOne(name = "location_id", targetColumnName = "id")
  public Location location;

  @R2dbcOneToMany(name = "id", targetEntity = House.class, targetColumn = "owner_id")
  public List<House> house = new ArrayList<>();


  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (House h : this.house) {
      sb.append(", ");
      sb.append(h.toString());
    }
    sb.replace(0, 2, "");

    return "User(id=" + this.id + ", name=" + this.name + ", parent=" + this.parent + ", createTime=" + this.createTime + ", location=" + this.location + ", house={" + sb.toString() + "})";
  }



}
