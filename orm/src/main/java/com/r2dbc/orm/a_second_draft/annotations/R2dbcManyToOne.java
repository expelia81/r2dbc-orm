package com.r2dbc.orm.a_second_draft.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 주의사항 : OneToMany를 쓰더라도, List로 선언하면 안되고 Object 자체로 선언해야함.
 * ManyToOne : name 필수입력
 * OneToMany : targetEntity, targetColumnName 필수입력
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface R2dbcManyToOne{

  @AliasFor("name")
  String value() default "id";

  /**
   * 테이블 자신의 칼럼명을 의미한다.
   */
  @AliasFor("value")
  String name() default "id";

  /**
   * 대상 테이블의 별칭.
   * 자기 참조 테이블의 경우에 반드시 지정해야한다.
   * 미정시 해당 테이블의 별칭은 기본 별칭을 따른다.
   */
  String alias() default "";

  /**
   * join 대상 테이블의 join 기준 컬럼명.
   *  (many to one : 타겟 엔티티의 pk 필드명. 기본값 : id)  -> 만약 타겟 엔티티의 pk가 id가 아닌 경우, pk 필드명 지정해야함.
   *  (one to many : 타겟 엔티티의 fk 필드명.)
   *  (many to many : 타겟 엔티티의 pk 필드명. 기본값 : id)
   */
  String targetColumnName() default "id";

  /**
   * one to many에서만 사용. join 대상 테이블의 엔티티 클래스.
   * 이것으로 list의 리플렉션 매핑을 한다.
   */
  Class<?> targetEntity() default Object.class;


  enum JoinType {
    MANY_TO_ONE,
    ONE_TO_MANY

  }
}
