package com.r2dbc.orm.first_draft.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface R2dbcTable {
  @AliasFor("name")
  String value() default "";

  @AliasFor("value")
  String name() default "";

  String alias() default "";
}
