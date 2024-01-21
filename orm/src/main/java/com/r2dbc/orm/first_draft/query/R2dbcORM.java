package com.r2dbc.orm.first_draft.query;

import com.r2dbc.orm.first_draft.annotations.R2dbcTable;
import com.r2dbc.orm.first_draft.annotations.R2dbcJoinColumn;
import com.r2dbc.orm.first_draft.exception.common.CommonExceptions;
import com.r2dbc.orm.first_draft.pageable.PageableUtils;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 엔티티의 동적 Join을 지원하기 위한 파이프라인 구현체.
 * 단, 검색 조건은 아직 단일 테이블에 대해서만 가능하다.
 * @see R2dbcTable 선언되지 않은 엔티티는 사용 불가.
 * @see R2dbcJoinColumn 조인하려면 필수이며, ManyToOne,OneToMany 지원. (단. 2중 oneToMany는 미지원.)
 * @see Id 선언되어있지 않은 엔티티는 에러가 발생할 수 있음.
 */
//@Slf4j
public interface R2dbcORM {

  /**
   * @param filter 검색 조건
   * @return 조건에 해당하는 데이터 전체 갯수
   */
  public static Mono<Long> count(Class<?> clazz, DatabaseClient client, Map<String, String> filter) {
    String tableName = clazz.getAnnotation(R2dbcTable.class).name();
    return client.sql(() -> QueryUtils.getCountQuery(tableName, filter))
                 .map((row, rowMetadata) -> row.get(0, Long.class))
                 .one();
  }

  /**
   * 메인 객체 외부에 있는 oneToMany는 올바르게 매핑되지 않음.
   * like 검색을 하고싶으면, 필드명에 "like,fieldname" 형식으로 넣으면 됨.
   * @param clazz 메인이 될 엔티티 클래스
   * @param client DatabaseClient 객체
   * @param filter 검색 조건(메인이 될 테이블의 칼럼만 가능하다). 각종 연산자를 통한 조건매핑도 가능하다. 기본은 = 이다.
   *               like / in / isNull / isNotNull / isTrue / isFalse / is / not / notNull / notLike / notIn
   *               --> key에 "조건,fieldname"
   *               between / notBetween
   *               --> key에 "조건,fieldname" value에 "value1,value2"
   * @param pageable 페이징 정보
   */
  public static <T> Mono<Page<T>> findByFilter(Class<T> clazz, DatabaseClient client, Map<String, String> filter, Pageable pageable) {
    return count(clazz, client, filter)
            .flatMap(count -> client.sql(() -> QueryUtils.getSingleSelectClauseWithJoin(clazz, null, null, false) + QueryUtils.getPagingByReflect(clazz, filter, pageable))
                .flatMap(result -> result.map((row, rowMetadata) -> MappingUtils.mapRowToEntityWithJoin(clazz, row, "", null, false, false)))
                .collectList()
                .flatMapMany(result -> JoinUtils.mergeFromResult(result, clazz)).collectList()
                .map(roles -> PageableUtils.getPageImplPaged(roles, pageable, count))
            );
  }

  /**
   * pageable을 사용하지 않는 필터 검색.
   */
  public static <T> Flux<T> findByFilter(Class<T> clazz, DatabaseClient client, Map<String, String> filter) {
    return client.sql(() -> QueryUtils.getSingleSelectClauseWithJoin(clazz, null, null, false) + QueryUtils.getPagingByReflect(clazz, filter,null))
            .map((row, rowMetadata) -> MappingUtils.mapRowToEntityWithJoin(clazz, row, "", null, false, false))
            .all()
            .collectList()
            .flatMapMany(result -> JoinUtils.mergeFromResult(result, clazz))
        ;
  }
  /**
   * ManyToMany 중간계층 조회용.
   * @param clazz 메인이 될 엔티티 클래스
   * @param client DatabaseClient 객체
   * @param filter 검색 조건(메인이 될 엔티티 클래스의 필드만 가능하다)
   */
  @Deprecated
  public static <T> Flux<T> findByForeignKeyForManyToMany(Class<T> clazz, DatabaseClient client, Map<String, String> filter) {
    return client.sql(QueryUtils.getSingleSelectClauseWithJoin(clazz, null, null, false) + QueryUtils.findByFilter(clazz, filter))
                                         .map((row, rowMetadata) -> MappingUtils.mapRowToEntityWithJoin(clazz, row, "", null, false, false))
                                         .all();
  }


  /**
   * id로 단일 데이터를 조회한다.
   * OneToMany, ManyToOne 모두 지원함.
   * 단, 2중 OneToMany는 아직 지원하지않으므로 주의. (OneToMany 관계의 객체 안에 또다른 OneToMany 관계의 객체가 있을 경우를 말함)
   */
  public static <T> Mono<T> findById(Class<T> clazz, DatabaseClient client, String id) {
    return client.sql(() -> QueryUtils.getSingleSelectClauseWithJoin(clazz, null, null, false) + QueryUtils.getFindByIdWhereQuery(clazz))
                 .bind("id", id)
                 .map((row, rowMetadata) -> MappingUtils.mapRowToEntityWithJoin(clazz, row, "", null, false, false))
                 .all()
                  .switchIfEmpty(Mono.error(new CommonExceptions.DataNotFoundException("해당 데이터가 존재하지 않습니다.")))
                  .collectList()
                  .flatMapMany(result -> JoinUtils.mergeFromResult(result, clazz))
                  .next();
  }

  /**
   * 데이터 전체를 조회한다.
   * id 순으로 정렬이 안될 경우 김창훈에게 문의.
   */
  public static <T> Flux<T> findAll(Class<T> clazz, DatabaseClient client) {
    return client.sql(() -> QueryUtils.getSingleSelectClauseWithJoin(clazz, null, null, false).toString())
        .map((row, rowMetadata) -> MappingUtils.mapRowToEntityWithJoin(clazz, row, "", null, false, false))
        .all()
        .collectList()
        .flatMapMany(result -> JoinUtils.mergeFromResult(result, clazz));
  }


}
