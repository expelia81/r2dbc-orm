package com.r2dbc.orm.a_second_draft.query;

import com.r2dbc.orm.annotations.R2dbcJoinColumn;
import com.r2dbc.orm.annotations.R2dbcManyToMany;
import com.r2dbc.orm.annotations.R2dbcTable;
import com.r2dbc.orm.first_draft.StringUtils;
import com.r2dbc.orm.first_draft.query.FieldUtils;
import com.r2dbc.orm.first_draft.query.JoinData;
import com.r2dbc.orm.first_draft.query.QueryWrapper;
import java.lang.reflect.Field;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;

public class SelectQueryCreator {

  /**
   * 순환 참조 막기위해서, 모든 테이블의 별칭이 기록된다.
   * 별칭은 빌드단계에서 고정되는 값이므로, 순환참조에서 무한한 반복은 방지된다.
   */
  public static QueryWrapper getSingleSelectClauseWithJoin(@NonNull Class<?> clazz, QueryWrapper existingQuery, String tableAlias, boolean alreadyOneToMany) {

    R2dbcTable mainTable = clazz.getAnnotation(R2dbcTable.class);

    String prefix = tableAlias!=null && !tableAlias.isBlank() ? tableAlias: mainTable.alias();
    String createdPrefix = prefix.isBlank() ? "" : prefix + ".";

    Field[] allFields = FieldUtils.getAllFields(clazz);

    /* 재귀가 null이면 쿼리 시작으로 간주한다. */
    QueryWrapper queryWrapper = existingQuery!=null ?
        existingQuery
        : new QueryWrapper(
            new StringBuilder(),
            new StringBuilder(" FROM " + mainTable.name() + " " + prefix + " ")
        );
    if (existingQuery==null) {
      queryWrapper.getQueriedAlias().add(mainTable.alias());
    }
    StringBuilder select = queryWrapper.getSelect();
    StringBuilder from = queryWrapper.getFrom();


//    /* 이미 쿼리된 타겟이라면 순환참조를 막기 위해 무시한다. */
//    if (queryWrapper.getQueriedAlias().contains(joinTableAlias)) {
//      log.info("순환 참조가 방지되었습니다. 별칭이 겹치는 조회는 순환참조로 간주되어 차단됩니다. : " + joinTableAlias);
//      log.info("차단된 시점의 별칭 : " + prefix);
//      continue;
//    }

    for (Field field : allFields) {

      /* @Slf4j 사용시, log라는 필드가 생겨나므로, 무시해야한다.*/
      if (field.getName().equals("log") || field.getName().equals("logger")) continue;

      /* 문법 에러 검사를 위한 임시 String 저장. */
      String temp = select.toString();

      /* Transient 걸려있다면 skip */
      if (field.isAnnotationPresent(Transient.class) && !field.isAnnotationPresent(R2dbcJoinColumn.class)) continue;
      if (field.isAnnotationPresent(R2dbcJoinColumn.class)) {
        R2dbcJoinColumn joinColumn = field.getAnnotation(R2dbcJoinColumn.class);

        /* OneToMany 안에 있는 OneToMany라면 걸러준다.*/
        if (alreadyOneToMany && joinColumn.joinType().equals(R2dbcJoinColumn.JoinType.ONE_TO_MANY)) continue;

        /* 연관 객체의 타입 파악. ManyToOne이면 원래 필드로, OneToMany면 타겟 엔티티로. */
        Class<?> targetEntity = joinColumn.joinType().equals(R2dbcJoinColumn.JoinType.MANY_TO_ONE) ? field.getType() : joinColumn.targetEntity();

        /* 칼럼명 기록. 칼럼명은 Join에서도 사용된다. */
        String columnName = joinColumn.name();

        /* 조인 칼럼에 별칭이 지정되어있다면 조인 칼럼의 별칭이 선택되고, 아니라면 R2dbcTable의 별칭을 사용한다.*/
        String joinTableAlias;
        try {
          joinTableAlias = joinColumn.alias().isBlank() ? targetEntity.getAnnotation(R2dbcTable.class).alias() : joinColumn.alias();
        } catch (NullPointerException e) {
          throw new RuntimeException("JoinColumn : {}에 어노테이션, R2dbcTable이 지정되어있지 않습니다.".replace("{}", field.getName()));
        }

        /* 테이블의 별칭이 중복되지 않는다면 체크하고, 조인 큐에 밀어넣는다. 중복된다면, 연관관계를 쿼리하지 않는다.*/
        if (!queryWrapper.getQueriedAlias().contains(joinTableAlias)) {
          queryWrapper.getQueriedAlias().add(joinTableAlias);
          queryWrapper.getJoinData().add(
              JoinData
                  .builder()
                  .alias(joinTableAlias)
                  .targetEntity(targetEntity)
                  .joinType(joinColumn.joinType())
                  .alreadyOneToMany(joinColumn.joinType().equals(R2dbcJoinColumn.JoinType.ONE_TO_MANY))
                  .build());
        } else {
          continue;
        }

        switch (joinColumn.joinType()) {
          case MANY_TO_ONE -> {
            from.append(" LEFT OUTER JOIN ")
                .append(targetEntity.getAnnotation(R2dbcTable.class).name())
                .append(" ").append(joinTableAlias)
                .append(" ON ")
                .append(prefix)
                .append(".")
                .append(columnName)
                .append(" = ")
                .append(joinTableAlias)
                .append(".")
                .append(joinColumn.targetColumnName());
            if (!temp.endsWith(", ") && !temp.isBlank()) {
              select.append(", ");
            }
            select.append(createdPrefix)
                .append(columnName)
                .append(prefix.isBlank() ? "" : " as " + prefix + "_" + columnName);
          }
          case ONE_TO_MANY -> {
            if(field.isAnnotationPresent(R2dbcManyToMany.class)) {
              R2dbcManyToMany manyToMany = field.getAnnotation(R2dbcManyToMany.class);

              String relationTableName = manyToMany.relationTableName();
              String relationTableAlias = StringUtils.isBlank(manyToMany.relationTableAlias()) ? "relation_"+relationTableName : manyToMany.relationTableAlias();
              String oneColumnName = manyToMany.oneColumnName();
              String manyColumnName = manyToMany.manyColumnName();

              from.append(" LEFT OUTER JOIN ")
                  .append(relationTableName) // 먼저, 중간 테이블을 메인 테이블과 join한다.
                  .append(" ")
                  .append(relationTableAlias)
                  .append(" ON ")
                  .append(prefix)
                  .append(".")
                  .append(columnName)
                  .append(" = ")
                  .append(relationTableAlias)
                  .append(".")
                  .append(oneColumnName);
              from.append(" LEFT JOIN ")
                  .append(targetEntity.getAnnotation(R2dbcTable.class).name()) // 그 다음, 타겟 테이블을 중간 테이블과 join한다.
                  .append(" ")
                  .append(joinTableAlias)
                  .append(" ON ")
                  .append(relationTableAlias)
                  .append(".")
                  .append(manyColumnName)
                  .append(" = ")
                  .append(joinTableAlias)
                  .append(".")
                  .append(joinColumn.targetColumnName());

            } else {
              from.append(" LEFT OUTER JOIN ")
                  .append(targetEntity.getAnnotation(R2dbcTable.class).name())
                  .append(" ")
                  .append(joinTableAlias)
                  .append(" ON ")
                  .append(prefix)
                  .append(".")
                  .append(columnName)
                  .append(" = ")
                  .append(joinTableAlias)
                  .append(".")
                  .append(joinColumn.targetColumnName());
            }
          }
        }

        continue;
      }

      /* 조회할 칼럼 생성 */
      Column annotation = field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class) : null;
      String column = annotation == null || annotation.value().isBlank() ? StringUtils.camelToSnake(field.getName()) : annotation.value();

      /* 사용된 별칭 추가 및 최종적인 칼럼 추가 */
      if (!temp.endsWith(", ") && !temp.isBlank()) select.append(", ");
      select.append(createdPrefix).append(column).append(prefix.isBlank() ? "" : " as " + prefix + "_" + column);
    }

    /* 조인 큐를 확인하고, join을 수행한다. */
    while (!queryWrapper.getJoinData().isEmpty()) {
      JoinData poll = queryWrapper.getJoinData().poll();
      if (poll != null){
        getSingleSelectClauseWithJoin(poll.getTargetEntity(), queryWrapper, poll.getAlias(), poll.isAlreadyOneToMany());
      }
    }

    return queryWrapper;
  }




  private static String getIdColumnName(Field[] fields){
    String id = null;
    for (Field field : fields) {
      if (field.isAnnotationPresent(Id.class)) {
        id = field.getName();
        break;
      }
    }
    if (id == null) throw new RuntimeException("org.springframework.data.annotation.Id 어노테이션이 지정된 필드가 없습니다.");
    return id;
  }

}