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
   * own column name    ex) user_id
   */
  @AliasFor("value")
  String name() default "id";

  /**
   * join target entity's alias name.
   * if empty, use target entity's table name.
   *
   * already mapped alias name is not searched.
   */
  String alias() default "";

  /**
   * join target entity's join condition column name
   *
   * if empty, use target entity's primary key column name (used @Id field's name).
   */
  String targetColumnName() default "";


}
