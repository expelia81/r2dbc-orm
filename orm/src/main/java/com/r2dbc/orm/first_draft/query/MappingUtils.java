package com.r2dbc.orm.first_draft.query;

import com.r2dbc.orm.first_draft.StringUtils;
import com.r2dbc.orm.first_draft.annotations.R2dbcJoinColumn;
import com.r2dbc.orm.first_draft.annotations.R2dbcTable;
import io.r2dbc.spi.Row;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;


public class MappingUtils {



  public static <T> T mapRowToEntityWithJoin(Class<T> clazz, Row row, String joinAlias, List<String> alreadyMappedAlias, boolean selfReference, boolean alreadyOneToMany) {
    R2dbcTable table = clazz.getAnnotation(R2dbcTable.class);

    List<String> mappedAlias = Objects.requireNonNullElseGet(alreadyMappedAlias, ArrayList::new);

    T entity;

    /* 별칭이 지정된 상태로 시작되는 경우에는 입력된 별칭을, 그렇지 않다면 엔티티에 지정된 별칭을 가져간다. */
    String defaultAlias = StringUtils.isBlank(joinAlias) ? table.alias() : joinAlias;
    String alias = defaultAlias.isBlank() ? "" : defaultAlias+"_";

    /* 최초 호출일 경우, 기본 엔티티의 별칭을 추가한다. */
    if (alreadyMappedAlias == null) {
      mappedAlias.add(defaultAlias);
    }

    try {
      entity = clazz.getDeclaredConstructor().newInstance();
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("해당 클래스에 기본 생성자가 존재하지 않습니다.");
    }

//    log.info("start mapping : " + entity.getClass().getSimpleName());
//    log.info("start mapping : " + defaultAlias);
//    mappedAlias.forEach(s -> log.info(s));

    Field[] fields = FieldUtils.getAllFields(clazz);

    for (Field field : fields) {
      /* TODO 순환참조 찾기 위한 로그*/
//      log.info(entity.getClass().getSimpleName());
//      log.info("field : " + field.getName());
      /* slf4j 사용시 넘어간다. */
      if (field.getName().equals("log") || field.getName().equals("logger")) continue;
      /* transient 필드는 무시되나, R2dbcJoinColumn과 같이 쓰면 무시되지 않는다. */
      if (field.isAnnotationPresent(Transient.class) && !field.isAnnotationPresent(R2dbcJoinColumn.class)) continue;
      if (field.isAnnotationPresent(R2dbcJoinColumn.class)) {
        /* 자기참조 객체인 경우에는 Join하지않는다. */
        if (selfReference) continue;
        R2dbcJoinColumn joinColumn = field.getAnnotation(R2dbcJoinColumn.class);
        /* OneToMany 안에 있는 OneToMany는 무시한다. */
        if (alreadyOneToMany && joinColumn.joinType().equals(R2dbcJoinColumn.JoinType.ONE_TO_MANY)) continue;

        /* 목표 엔티티 클래스를 찾는다. oneToMany일 경우 List 형태이므로, 어노테이션에서 정보를 얻는다.*/
        Class<?> targetEntity = joinColumn.joinType().equals(R2dbcJoinColumn.JoinType.MANY_TO_ONE) ? field.getType() : joinColumn.targetEntity();
        String joinTableAlias = joinColumn.alias().isBlank() ? targetEntity.getAnnotation(R2dbcTable.class).alias() : joinColumn.alias();

        /* 이미 탐색된 별칭일 경우에는 스킵하고, 처음 만나는 별칭이면 매핑된 별칭에 추가한다. */
        if (mappedAlias.contains(joinTableAlias)) {
          continue;
        } else {
          mappedAlias.add(joinTableAlias);
        }

        Object joinEntity;

        /* 연관관계인 객체 매핑 시작. 만약 자기참조하는 객체인 경우에는, 해당 자식 객체에서는 연관관계를 매핑하지 않는다. */
        if (joinColumn.joinType().equals(R2dbcJoinColumn.JoinType.ONE_TO_MANY)) {

//          log.info("last onetomany joined : " + defaultAlias + " ==> " + targetEntity.getAnnotation(R2dbcTable.class).alias());
          Object target = mapRowToEntityWithJoin(targetEntity, row, joinTableAlias, mappedAlias, targetEntity.equals(clazz), true);
          joinEntity = target != null ? List.of(target) : new ArrayList<>();
        } else {
//          log.info("last manytoone joined : " + defaultAlias + " ==> " + targetEntity.getAnnotation(R2dbcTable.class).alias());
          joinEntity = mapRowToEntityWithJoin(targetEntity, row, joinTableAlias, mappedAlias, targetEntity.equals(clazz), alreadyOneToMany);
        }
        field.setAccessible(true);
        try {
          field.set(entity, joinEntity);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
        continue;
      }

      /* 필드명을 컬럼명으로 변환 */
      String columnName = StringUtils.camelToSnake(field.getName());
      if (field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).value().isBlank()) {
        columnName = field.getAnnotation(Column.class).value();
      }

      /* 별칭이 존재하면 별칭을 컬럼명 앞에 붙임
       * row에서 값 추출 */
      Object value = row.get(alias+columnName);

      /* 혹시 값이 null이라면 다음으로. id값이 null이면 아예 객체 자체를 비운다. */
      if (value == null && field.isAnnotationPresent(Id.class)) {
        return null;
      }
      if (value == null) continue;

      /* String 값을 Enum으로 매핑 */
      if (field.getType().isEnum()) {
        String enumName = String.valueOf(value);
        try {
          Enum<?> targetValue = Enum.valueOf((Class<Enum>)field.getType(), enumName);
          value = targetValue;
        } catch(IllegalArgumentException e) {
          e.printStackTrace();
        }
      }

      /* 값이 null이 아니라면 필드값 주입 */
      field.setAccessible(true);
      try {
        field.set(entity, value);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    return entity;
  }

}
