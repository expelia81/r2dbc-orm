package com.r2dbc.orm.a_second_draft;

import com.r2dbc.orm.a_second_draft.query.QueryCreator;
import com.r2dbc.orm.a_second_draft.query.SimpleQueryCreator;

public class QueryFactory {
  private static final QueryCreator simple = new SimpleQueryCreator();


  public static QueryCreator simple() {
    return simple;
  }

}
