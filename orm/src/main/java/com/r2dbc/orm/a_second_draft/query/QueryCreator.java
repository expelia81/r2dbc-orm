package com.r2dbc.orm.a_second_draft.query;

import java.util.Map;
import org.springframework.data.domain.Pageable;

public interface QueryCreator {

  <T> String count(Class<T> entityClass);

  <T, ID> String select(Class<T> entityClass, Class<ID> idClass);

  <T, ID> String findById(Class<T> entityClass, Class<ID> idClass);

  <T> String allPage(Class<T> entityClass, Pageable pageable);
  <T> String where(Class<T> entityClass, Map<String, String> filter);
  <T> String wherePage(Class<T> entityClass, Pageable pageable);
}
