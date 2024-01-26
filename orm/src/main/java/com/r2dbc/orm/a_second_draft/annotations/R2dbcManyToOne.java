package com.r2dbc.orm.a_second_draft.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 주의사항 : OneToMany를 쓰더라도, List로 선언하면 안되고 Object 자체로 선언해야함.
 * ManyToOne : name 필수입력
 * OneToMany : targetEntity, targetColumnName 필수입력
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
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
   * join 대상 테이블의 별칭.
   * 자기 참조 테이블의 경우에 반드시 지정해야한다.
   * 미정시 해당 테이블의 별칭은 기본 별칭을 따른다.
   */
  String alias() default "";

  /**
   * join 대상 테이블의 join 기준 컬럼명.
   * 비어있을 경우, 해당 엔티티의 @Id 칼럼명을 따른다.
   */
  String targetColumnName() default "";


}
