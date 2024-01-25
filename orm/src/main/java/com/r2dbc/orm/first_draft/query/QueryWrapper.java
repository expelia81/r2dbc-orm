package com.r2dbc.orm.first_draft.query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.r2dbc.orm.a_second_draft.utils.StringUtils;
import com.r2dbc.orm.a_second_draft.annotations.R2dbcTable;
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
  private final List<String> queriedAlias = new ArrayList<>();

  private final Queue<JoinData> joinData = new LinkedList<>();

  @Override
  public String toString() {
    String select = this.select.toString();
    if (select.endsWith(",")) {
      select = select.substring(0, select.length() - 1);
    }
    return "SELECT " + select.toString() + " " + from.toString() + " ";
  }

  public QueryWrapper(StringBuilder select, StringBuilder from) {
    this.select = select;
    this.from = from;
  }

  public static QueryWrapper create(R2dbcTable mainTable) {
    String alias;
    if (mainTable == null) {
      log.error("mainTable is null");
      throw new IllegalArgumentException("mainTable is null");
    }

    if (StringUtils.isBlank(mainTable.alias())) {
      alias = mainTable.name();
    } else {
      alias = mainTable.alias();
    }
    return new QueryWrapper( new StringBuilder(), new StringBuilder(" FROM " + mainTable.name() + " " + mainTable.alias() + " "));
  }
}
