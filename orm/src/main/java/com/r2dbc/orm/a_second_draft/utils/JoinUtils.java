package com.r2dbc.orm.a_second_draft.utils;

import com.r2dbc.orm.a_second_draft.utils.FieldUtils;
import com.r2dbc.orm.first_draft.annotations.R2dbcJoinColumn;
import com.r2dbc.orm.first_draft.annotations.R2dbcTable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.r2dbc.orm.first_draft.query.MappingUtils;
import com.r2dbc.orm.first_draft.query.QueryUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import reactor.core.publisher.Flux;

/**
 * DB에서의 JOIN을 직접적으로 지원하지 않는 R2DBC를 위한 JOIN 유틸리티.
 * 쿼리에 관여하는 기능의 제공은 이하 유틸클래스 참고.
 * @see QueryUtils ,
 * @see MappingUtils
 */
@Slf4j
public class JoinUtils {


  @SneakyThrows
  public static <T> Flux<T> mergeFromResult(List<T> result, Class<T> clazz) {
    // 입력된 순서를 유지해야한다.
    Map<String, List<T>> map = new LinkedHashMap<>();

    Field idField = null;
    for (Field field : FieldUtils.getAllFields(clazz)) {
      if (field.isAnnotationPresent(Id.class)){
        idField = field;
      }
    }
    //여기까지 왔으면 idField는 null일 수 없다.
    assert idField != null;
    idField.setAccessible(true);

    /* id 필드를 기준으로, 결과 객체들을 묶어낸다. */
    for (T obj : result) {
      String id = idField.get(obj).toString();
      if (map.containsKey(id)) {
        map.get(id).add(obj);
      } else {
        List<T> list = new ArrayList<>();
        list.add(obj);
        map.put(id, list);
      }
    }
    return Flux.fromIterable(map.values())
        .map(objects -> mergeOneToManys(objects));
  }

  /**
   * 1:N 관계의 객체를 합칠 수 있도록 한다.
   * 주의) 1:N 관계의 객체는 반드시 List로 설정되어야만 한다.
   */
  public static <T> T mergeOneToManys(List<T> objects) {
    if (objects.isEmpty()) throw new IllegalArgumentException("Empty List is can't exist in JoinUtils.mergeOneToMany. Check query.");

    T result = objects.get(0);

    if (objects.size() == 1) return result;

    Field[] fields = FieldUtils.getAllFields(result.getClass());

    String mainAlias = result.getClass().getAnnotation(R2dbcTable.class).alias();

//    if (alreadyMappedAlias == null) {
//      alreadyMappedAlias = new HashSet<>();
//      alreadyMappedAlias.add(mainAlias);
//    }


    try {
    /* size가 2 이상인 경우, OneToMany 연관관계를 가진 객체들을 찾는다.
     * 연관 관계를 가진 객체의 경우, list에서 빼서 result에 삽입한다. */
      for (Field field: fields) {
        /* oneToMany인 경우, 해당 필드의 리스트를 꺼내고, 리스트에 객체들을 추가한다. */
        /* oneToMany이면 재귀적 호출을 하지 않는다. */
        if (field.isAnnotationPresent(R2dbcJoinColumn.class) && field.getAnnotation(R2dbcJoinColumn.class).joinType().equals(R2dbcJoinColumn.JoinType.ONE_TO_MANY)) {
          field.setAccessible(true);
          List<Object> list = new ArrayList<>();

          /* 리스트를 순회하며, 원투매니 객체들을 하나로 합친다. */
          for (T object : objects) {
            List<Object> tempList = (List<Object>) field.get(object);
            if (tempList == null || tempList.isEmpty()) continue;
            else list.addAll(tempList);
          }

          // onetomany가 2개 이상 존재할 경우에는 중복이 발생할 수 있으므로, 중복을 제거한다. (hashcode, equals 정의되어야한다)
          field.set(result, list.stream().distinct().toList());

        } else if (field.isAnnotationPresent(R2dbcJoinColumn.class) && field.getAnnotation(R2dbcJoinColumn.class).joinType().equals(R2dbcJoinColumn.JoinType.MANY_TO_ONE)) {
          // manyToOne은 반드시 매핑되는 객체이므로, objects와 항상 크기가 같다.
          // oneToMany 속 oneToMany를 고려하지 않는다면 항상 찾아낼 수 있다.
          field.setAccessible(true);
          if (field.get(result) == null) continue;

          /* ManyToOne 객체가 발견되면, 해당 필드를 대상으로 하는 새로운 객체 리스트를 만든다. */
          List<Object> list = new ArrayList<>();
          for (T object : objects) {
            list.add(field.get(object));
          }

          /* OneToMany를 더 찾기위한 재귀 탐색을 실시한다. */
          field.set(result, mergeOneToManys(list));

        }
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    return result;
  }

}
