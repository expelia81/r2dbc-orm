package com.r2dbc.orm.a_second_draft.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.r2dbc.orm.a_second_draft.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FieldUtils {
  public static Field[] getAllFields(Class<?> clazz) {

    return getFields(clazz, new ArrayList<>());
  }

  private static Field[] getFields(Class<?> clazz, List<Field> result) {
    result.addAll(Arrays.asList(clazz.getDeclaredFields()));
    /* 슈퍼 클래스가 Object면 바로 리턴. */
    if (clazz.getSuperclass().equals(Object.class)) return result.toArray(new Field[0]);
    else {
      return getFields(clazz.getSuperclass(), result);
    }
  }

  public static void addSelectFieldQuery(QueryWrapper query, Field field, String alias) {
  }

  public static <T> void setField(T entity, Field field, Object value) {
    if (value == null) return;
    try {
      field.setAccessible(true);
      field.set(entity, value);
    } catch (IllegalAccessException | IllegalArgumentException e) {
      if (value instanceof List && !field.getType().equals(List.class)){
        log.error("oneToMany field must be List Type : "+entity.getClass().getSimpleName() +" - "+field.getName());
      } else {
        log.error("can't set field : "+entity.getClass().getSimpleName() +" - "+field.getName());
      }
      log.error("but continue to mapping other fields");
    }
  }
}
