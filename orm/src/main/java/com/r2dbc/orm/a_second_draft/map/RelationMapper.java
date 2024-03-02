package com.r2dbc.orm.a_second_draft.map;

import io.r2dbc.spi.Readable;
import org.springframework.r2dbc.core.DatabaseClient;

public interface RelationMapper {

  RelationMapper simple = new SimpleRelationMapper();

  <T> T toEntity(Readable row, Class<T> entityClass, DatabaseClient client);

  static RelationMapper simple() {
    return simple;
  }
}
