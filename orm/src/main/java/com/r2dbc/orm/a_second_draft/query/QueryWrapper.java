package com.r2dbc.orm.a_second_draft.query;

import java.lang.reflect.Field;
import java.util.*;

import com.r2dbc.orm.a_second_draft.annotations.R2dbcManyToOne;
import com.r2dbc.orm.a_second_draft.query.join.JoinData;
import com.r2dbc.orm.a_second_draft.query.creator.QueryCreator;
import com.r2dbc.orm.a_second_draft.query.join.R2oJoinType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor
@Slf4j
public class QueryWrapper {
  private StringBuilder select;
  private StringBuilder from;
  private final Set<String> queriedAlias = new HashSet<>();

  private final Queue<JoinData> JoinQueue = new LinkedList<>();
  private QueryCreator queryCreator;

  @Override
  public String toString() {
    String select = this.select.toString().trim();
    if (select.endsWith(",")) {
      select = select.substring(0, select.length() - 1);
    }

    String result = "SELECT " + select + " " + from.toString() + " ";
//    log.trace("query : {}", result);
    return result;
  }

  public QueryWrapper(StringBuilder select, StringBuilder from, QueryCreator queryCreator) {
    this.select = select;
    this.from = from;
    this.queryCreator = queryCreator;
  }

  public static QueryWrapper create(Class<?> mainTable, QueryCreator queryCreator) {
    if (mainTable == null) {
      log.error("mainTable is null");
      throw new IllegalArgumentException("mainTable is null");
    }

    String alias = R2oTableUtils.getTableAlias(mainTable);
    String name = R2oTableUtils.getTableName(mainTable);

    QueryWrapper queryWrapper = new QueryWrapper( new StringBuilder(), new StringBuilder(" FROM " + name + " " + alias + " "), queryCreator);

    queryWrapper.queriedAlias.add(alias);

    return queryWrapper;
  }

  public boolean isJoined(String joinTargetAlias) {
    return queriedAlias.contains(joinTargetAlias);
  }
}
