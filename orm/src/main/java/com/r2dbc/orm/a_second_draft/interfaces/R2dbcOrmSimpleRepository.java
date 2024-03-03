package com.r2dbc.orm.a_second_draft.interfaces;

import com.r2dbc.orm.a_second_draft.QueryFactory;
import com.r2dbc.orm.a_second_draft.map.RelationMapper;
import com.r2dbc.orm.a_second_draft.map.merge.R2oMergeUtils;
import com.r2dbc.orm.a_second_draft.query.creator.QueryCreator;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * OneToMany,ManyToMany를 타겟 엔티티 내부에서만 서치하는 심플한 레포지터리.
 * @param <T> 타겟 엔티티 타입
 * @param <ID> 타겟 엔티티의 Id 필드 타입
 */
@Slf4j
public class R2dbcOrmSimpleRepository<T, ID>
        implements R2dbcOrmRepository<T, ID>
        {

	private final Class<T> entityClass;
  private final Class<ID> idClass;

  @Getter
	private final String selectQuery;
	private final RelationMapper relationMapper;


  /**
   * 요구 조건
   * 1. db client 자동 주입
   * 2. 해당 인터페이스 구현체가 생성될 때, 메소드 내용 덮어쓴 가짜 객체를 대신 주입받도록 수정. (아마, 이 부분에서 )
   */
  public R2dbcOrmSimpleRepository(Class<T> entityClass, Class<ID> idClass) {
		QueryCreator queryCreator = QueryFactory.simple();
    this.entityClass = entityClass;
    this.idClass = idClass;
		this.selectQuery = queryCreator.createSelectQueryWithJoin(entityClass).toString();
		this.relationMapper = RelationMapper.simple();
		//    this.countQuery = queryCreator.count(entityClass);
//    this.selectQuery = queryCreator.select(entityClass, idClass);
//    this.findByIdQuery = selectQuery + queryCreator.filterById(entityClass, idClass);
  }

	@Override
	public Flux<T> findAll(DatabaseClient client) {
		return client.sql(selectQuery)
						.map(row -> relationMapper.toEntity(row, entityClass, client))
						.all()
						.collectList()
						.flatMapMany(result -> R2oMergeUtils.mergeFromResult(result, entityClass))
						.log();
	}

					//  @Override
//  public Mono<T> findById(ID id, DatabaseClient client) {
//    return client.sql(findByIdQuery)
//          .bind("id", id)
//          .map(row -> RelationMapper.toEntity(row, entityClass, client))
//          .one();
//  }
//
//  @Override
//  public Flux<T> findAll(DatabaseClient client) {
//    return client.sql()
//        .map(row -> RelationMapper.toEntity(row, entityClass, client))
//        .all();
//  }
//
//  @Override
//  public Flux<T> findAll(DatabaseClient client, Pageable pageable) {
////    return client.sql(selectQuery + )
////        .map(row -> RelationMapper.toEntity(row, entityClass, client))
////        .all();
//    return null;
//  }
//
//  @Override
//  public Flux<T> findByFilter(DatabaseClient client, Map<String, String> filter) {
////    return client.sql(selectQuery + queryCreator.where(entityClass, filter))
////        .map(row -> RelationMapper.toEntity(row, entityClass, client))
////        .all();
//    return null;
//  }
//
//  @Override
//  public Flux<T> findByFilter(DatabaseClient client, Map<String, String> filter,
//      Pageable pageable) {
//    return null;
////    return client.sql(selectQuery + queryCreator.where(entityClass, filter) + queryCreator.)
////        .map(row -> RelationMapper.toEntity(row, entityClass, client))
////        .all();
//  }
}
