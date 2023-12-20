package com.r2dbc.orm.annotations;

import com.r2dbc.orm.annotations.R2dbcJoinColumn.JoinType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface R2dbcOneToMany {
  @AliasFor("name")
  String value() default "";

  /**
   * 비어있어도 관계는 없으나, 입력할 경우 쿼리 생성이 더 빨라진다.
   * 의미하는 값은 one 측의 join 기준 칼럼(보통 pk 값을 의미함.)
   * 비어있을 경우, 해당 객체의 id 이름이 대신 입력된다.
   */
  @AliasFor("value")
  String name() default "";

  /**
   * 대상 테이블의 별칭.
   * 자기 참조 테이블의 경우에 반드시 지정해야한다.
   * 미정시 해당 테이블의 별칭은 기본 별칭을 따른다.
   */
  String alias() default "";

  /**
   * 해당 테이블을 참조하는 외래키 칼럼의 이름을 의미한다.
   */
  String targetColumn() default "id";

  /**
   * join 대상 테이블의 엔티티 클래스.
   */
  Class<?> targetEntity() default Object.class;
}
