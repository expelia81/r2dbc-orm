package com.r2dbc.orm.a_second_draft.query;

import java.util.Map;

public interface QueryCreator {

  <T> String count(Class<T> entityClass);

  <T, ID> String select(Class<T> entityClass, Class<ID> idClass);

  /** id 조건문만 포함 */
  <T, ID> String filterById(Class<T> entityClass, Class<ID> idClass);

  /** where절 in 서브쿼리로 정렬 및 페이징 후 최종 결과물 재정렬 */
  <T> String paging(Class<T> entityClass);
  
  /** where절 생성 */
  <T> String filter(Class<T> entityClass, Map<String, String> filter);
  
  /** in 서브쿼리 필터 및 정렬 후 검색, 다시 정렬 및 페이징 */
  <T> String filterWithPaging(Class<T> entityClass, Map<String, String> filter);
}
