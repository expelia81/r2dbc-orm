package com.r2dbc.orm.a_second_draft.interfaces;

import com.r2dbc.orm.annotations.R2dbcTable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * OneToMany,ManyToMany를 타겟 엔티티 내부에서만 서치하는 심플한 레포지터리.
 * @param <T> 타겟 엔티티 타입
 * @param <ID> 타겟 엔티티의 Id 필드 타입
 */
public interface R2dbcOrmSimpleRepository<T, ID> {

  /**
   * 요구 조건
   * 1. db client 자동 주입
   * 2. 해당 인터페이스 구현체가 생성될 때, 메소드 내용 덮어쓴 가짜 객체를 대신 주입받도록 수정. (아마, 이 부분에서 )
   */

  DatabaseClient client = null;


  Mono<T> findById(ID id, DatabaseClient client);
  Flux<T> findAll(DatabaseClient client);
  Flux<T> findAll(DatabaseClient client, Pageable pageable);
  Flux<T> findByFilter(DatabaseClient client, Map<String, String> filter);
  Flux<T> findByFilter(DatabaseClient client, Map<String, String> filter, Pageable pageable);



  static <T,ID> R2dbcOrmSimpleRepository<T,ID> simple() {
    return new R2dbcOrmSimpleRepository<>() {
      @Override
      public Mono<T> findById(ID id, DatabaseClient client) {
        return null;
      }

      @Override
      public Flux<T> findAll(DatabaseClient client) {
        return null;
      }

      @Override
      public Flux<T> findAll(DatabaseClient client, Pageable pageable) {
        return null;
      }

      @Override
      public Flux<T> findByFilter(DatabaseClient client, Map<String, String> filter) {
        return null;
      }

      @Override
      public Flux<T> findByFilter(DatabaseClient client, Map<String, String> filter,
          Pageable pageable) {
        return null;
      }
    };
  }

  static <T,ID> R2dbcOrmSimpleRepository<T,ID> extended() {
    return new R2dbcOrmSimpleRepository<T,ID>() {
      private final Type[] sample = getClass().getGenericInterfaces();
      @Override
      public Mono<T> findById(ID id, DatabaseClient client) {
        return null;
      }

      @Override
      public Flux<T> findAll(DatabaseClient client) {

        ParameterizedTypeReference<T> superClass = new ParameterizedTypeReference<T>() {
        };
        Class<?> clazz = this.getClass();

        // 클래스의 제네릭 수집
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
          if (genericInterface instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericInterface;

            // 제네릭 타입의 타입 매개변수들
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            for (Type argument : typeArguments) {
              System.out.println("Type Argument: " + argument.getTypeName());
            }
          }
        }


//        System.out.println("type = " + type.getTypeName());

//        String name = ((Class<T>) superClass.getActualTypeArguments()[0]).getTypeName();
//        String name2 = ((Class<ID>) superClass.getActualTypeArguments()[1]).getTypeName();

//        System.out.println("name = " + name);
//        System.out.println("name2 = " + name2);
//        String name2 = sample[1].getTypeName();
//        System.out.println("name2 = " + name2);
//        String sql = "SELECT * FROM " + input.getAnnotation(R2dbcTable.class).value();
//        System.out.println("sql = " + sql);
//        return client.sql(sql)
//            .map((row, rowMetadata) -> null)
//            .all();
        return Flux.empty();
      }

      @Override
      public Flux<T> findAll(DatabaseClient client, Pageable pageable) {
        return null;
      }

      @Override
      public Flux<T> findByFilter(DatabaseClient client, Map<String, String> filter) {
        return null;
      }

      @Override
      public Flux<T> findByFilter(DatabaseClient client, Map<String, String> filter,
          Pageable pageable) {
        return null;
      }
    };
  }
  static <T,ID> R2dbcOrmSimpleRepository<T,ID> complete() {
    return new R2dbcOrmSimpleRepository<>() {
      @Override
      public Mono<T> findById(ID id, DatabaseClient client) {
        return null;
      }

      @Override
      public Flux<T> findAll(DatabaseClient client) {
        return null;
      }

      @Override
      public Flux<T> findAll(DatabaseClient client, Pageable pageable) {
        return null;
      }

      @Override
      public Flux<T> findByFilter(DatabaseClient client, Map<String, String> filter) {
        return null;
      }

      @Override
      public Flux<T> findByFilter(DatabaseClient client, Map<String, String> filter,
          Pageable pageable) {
        return null;
      }
    };
  }
}
