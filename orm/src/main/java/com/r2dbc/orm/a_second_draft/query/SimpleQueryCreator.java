package com.r2dbc.orm.a_second_draft.query;

import com.r2dbc.orm.a_second_draft.query.QueryCreator;
import java.util.Map;

public class SimpleQueryCreator implements QueryCreator {

  @Override
  public <T> String count(Class<T> entityClass) {
    return null;
  }

  @Override
  public <T, ID> String select(Class<T> entityClass, Class<ID> idClass) {
    return null;
  }

  @Override
  public <T, ID> String filterById(Class<T> entityClass, Class<ID> idClass) {
    return null;
  }

  @Override
  public <T> String paging(Class<T> entityClass) {
    return null;
  }

  @Override
  public <T> String filter(Class<T> entityClass, Map<String, String> filter) {
    return null;
  }

  @Override
  public <T> String filterWithPaging(Class<T> entityClass, Map<String, String> filter) {
    return null;
  }
}
