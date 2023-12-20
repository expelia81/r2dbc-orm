package com.r2dbc.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Many To Many 관계로밖에 one to many를 표현할 수 없는 경우
 *  -> @R2dbcJoinColumn(JoinType.ONE_TO_MANY)와 병기하여 사용한다.
 * 중간 관계 테이블에 대한 정보로서, join 조건으로만 활용된다.
 * one 역할이 될 테이블과 many 역할이 될 테이블에 대한 정보는 R2dbcJoinColumn에 의존한다.
 * @see R2dbcJoinColumn
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface R2dbcManyToMany {

  /**
   * 중간 관계 테이블 이름
   */
  String relationTableName() default "";

  /**
   * 중간 관계 테이블의 별칭
   * 비어있다면 relationTableName을 별칭으로 사용
   */
  String relationTableAlias() default "";

  /**
   * 중간 관계 테이블 내에서, 현재 테이블의 fk 필드명
   */
  String oneColumnName() default "";

  /**
   * 중간 관계 테이블 내에서, 타겟 테이블의 fk 필드명
   */
  String manyColumnName() default "";
}
