package com.r2dbc.orm.first_draft.query;

import java.util.*;

import com.r2dbc.orm.a_second_draft.query.R2oTableUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor
@Builder
@Slf4j
public class QueryWrapper {
  private StringBuilder select;
  private StringBuilder from;
  private final Set<String> queriedAlias = new HashSet<>();

  private final Queue<JoinData> joinData = new LinkedList<>();

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

  public QueryWrapper(StringBuilder select, StringBuilder from) {
    this.select = select;
    this.from = from;
  }

  public static QueryWrapper create(Class<?> mainTable) {
    if (mainTable == null) {
      log.error("mainTable is null");
      throw new IllegalArgumentException("mainTable is null");
    }

    String alias = R2oTableUtils.getTableAlias(mainTable);
    String name = R2oTableUtils.getTableName(mainTable);

    QueryWrapper queryWrapper = new QueryWrapper( new StringBuilder(), new StringBuilder(" FROM " + name + " " + alias + " "));

    queryWrapper.queriedAlias.add(alias);

    return queryWrapper;
  }

  public boolean isJoined(String joinTargetAlias) {
    return queriedAlias.contains(joinTargetAlias);
  }
}
