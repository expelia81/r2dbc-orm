package com.r2dbc.orm.a_second_draft.interfaces;

import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/**
 * 모든 depth에서 연관관계 매핑을 싹 다 수행한다.
 * @param <T> 타겟 엔티티 타입
 * @param <ID> 타겟 엔티티의 Id 필드 타입
 */
public interface R2dbcOrmRepository<T, ID> {

  /**
   * 요구 조건
   * 1. db client 자동 주입
   * 2. 해당 인터페이스 구현체가 생성될 때, 메소드 내용 덮어쓴 가짜 객체를 대신 주입받도록 수정. (아마, 이 부분에서 )
   */


  Mono<T> findById(ID id, DatabaseClient client);
  Flux<T> findAll(DatabaseClient client);
  Flux<T> findAll(DatabaseClient client, Pageable pageable);
  Flux<T> findByFilter(DatabaseClient client, Map<String, String> filter);
  Flux<T> findByFilter(DatabaseClient client, Map<String, String> filter, Pageable pageable);

  static <T,ID> R2dbcOrmRepository<T,ID> simple(Class<T> entityClass, Class<ID> idClass) {
    return new R2dbcOrmSimpleRepository<T, ID>(entityClass, idClass);
  }
}
