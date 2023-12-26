package com.r2dbc.orm.a_second_draft.query.impl;

import com.r2dbc.orm.a_second_draft.query.QueryCreator;

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
  public <T, ID> String findById(Class<T> entityClass, Class<ID> idClass) {
    return null;
  }
}
