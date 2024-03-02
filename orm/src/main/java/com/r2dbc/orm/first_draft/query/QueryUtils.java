package com.r2dbc.orm.first_draft.query;

import com.r2dbc.orm.a_second_draft.query.join.JoinData;
import com.r2dbc.orm.a_second_draft.query.QueryWrapper;
import com.r2dbc.orm.a_second_draft.utils.FieldUtils;
import com.r2dbc.orm.a_second_draft.utils.R2oStringUtils;
import com.r2dbc.orm.first_draft.annotations.R2dbcJoinColumn;
import com.r2dbc.orm.first_draft.annotations.R2dbcManyToMany;
import com.r2dbc.orm.first_draft.annotations.R2dbcTable;
import com.r2dbc.orm.a_second_draft.utils.PageableUtils;
import java.lang.reflect.Field;
import java.util.Map;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.mapping.Column;
//@Slf4j
public class QueryUtils {
  /**
   * where절을 생성하는데 사용됨.
   * 하지만, 성능에 민감한 쿼리의 경우에는 where절 순서를 지정할 수 없으므로 사용을 지양할 것.
   * 별칭별로 1번씩 호출해야한다(single로 작명된 이유).
   * @param existingWhere : where절이 이미 존재하는 경우에만 입력. 없으면 null
   * @param tableAlias : 셀렉트 절의 테이블 별칭. '.'은 빼고 별칭만 입력. null로 입력시 별칭 미사용
   * @param filter : 필터링 조건. db에는 key = value 형태로 입력된다. 최초 조건이 아닌 경우, and 삽입. 단, key에 like,가 들어가고 value의 입력이 %value%일 경우, like로 대신 삽입된다.
   * @return : where절을 리턴함. select절과 order, group절은 포함되지 않으므로 주의해야함.
   */
  public static String getSingleWhereClause(String existingWhere, String tableAlias, @NonNull Map<String, String> filter) {

    /* 선행 where절이 입력되었다면 추가함.(여러 tableAlias 적용 위함) */
    StringBuilder where = new StringBuilder(existingWhere == null || existingWhere.isBlank() ? " where " : existingWhere);

    /* 별칭 생성 */
    String alias = tableAlias!=null && !tableAlias.isBlank() ? tableAlias+"." : "";

    /* 필터 갯수만큼 조건절에 추가 */
    for (String key : filter.keySet()) {
      String value = filter.get(key);

      /* 개발자 실수 고려, 값이 누락되면 쿼리에 넣지 않음. */
      if (value==null || value.isBlank()) continue;

      /* 반복되는 호출이 있을 수 있으므로, and 키워드는 항상 추가하는 것이 아니라, 새로운 조건이 추가될 때를 기준으로 추가한다. */
      if (!where.toString().endsWith(" where ")) where.append(" and ");

      /* 조건 추가 */
      extracted(where, alias, key, value);

    }

    /* 조건절 누락됐다면 where 제거. */
    return where.toString().endsWith(" where ") ? "" : where.toString();

  }

  private static void extracted(StringBuilder where, String alias, String key, String value) {
    if (key.split(",").length==1) {
      where.append(alias).append(R2oStringUtils.camelToSnake(key)).append(" = ").append("'").append(
          value).append("'");
    } else {
      String condition = key.split(",")[0];
      String realKey = key.split(",")[1];
      switch (condition) {
        case "like" -> {
          if (value.startsWith("%") && value.endsWith("%"))
            where.append(alias).append(R2oStringUtils.camelToSnake(realKey)).append(" like ").append("'").append(
                value).append("'");
          else if (value.startsWith("%"))
            where.append(alias).append(R2oStringUtils.camelToSnake(realKey)).append(" like ").append("'%").append(
                value).append("'");
          else if (value.endsWith("%"))
            where.append(alias).append(R2oStringUtils.camelToSnake(realKey)).append(" like ").append("'").append(
                value).append("%'");
          else
            where.append(alias).append(R2oStringUtils.camelToSnake(realKey)).append(" like ").append("'").append(
                value).append("'");
        }
        case "not" -> where.append(alias).append(R2oStringUtils.camelToSnake(realKey)).append(" != ").append("'").append(value).append("'");
        case "gt" -> where.append(alias).append(R2oStringUtils.camelToSnake(realKey)).append(" > ").append("'").append(value).append("'");
        case "gte" -> where.append(alias).append(R2oStringUtils.camelToSnake(realKey)).append(" >= ").append("'").append(value).append("'");
        case "lt" -> where.append(alias).append(R2oStringUtils.camelToSnake(realKey)).append(" < ").append("'").append(value).append("'");
        case "lte" -> where.append(alias).append(R2oStringUtils.camelToSnake(realKey)).append(" <= ").append("'").append(value).append("'");
        case "in" -> where.append(alias).append(R2oStringUtils.camelToSnake(realKey)).append(" in ").append("(").append(value).append(")");
        case "notIn" -> where.append(alias).append(R2oStringUtils.camelToSnake(realKey)).append(" not in ").append("(").append(value).append(")");
        case "isNull" -> where.append(alias).append(R2oStringUtils.camelToSnake(realKey)).append(" is null ");
        case "isNotNull" -> where.append(alias).append(R2oStringUtils.camelToSnake(realKey)).append(" is not null ");
        case "between" -> {
          String value1 = value.split(",")[0];
          String value2 = value.split(",")[1];
          where.append(alias).append(R2oStringUtils.camelToSnake(realKey)).append(" between ").append("'").append(value1).append("'").append(" and ").append("'").append(value2).append("'");
        }
        case "notBetween" -> {
          String value1 = value.split(",")[0];
          String value2 = value.split(",")[1];
          where.append(alias).append(R2oStringUtils.camelToSnake(realKey)).append(" not between ").append("'").append(value1).append("'").append(" and ").append("'").append(value2).append("'");
        }
        default -> throw new IllegalStateException("Filter에 들어가는 검색 조건이 문법에 맞지 않습니다. " + condition);
      }
    }
  }

  /**
   * 조건에 맞는 id를 걸러내는 조건절을 만든 후, 그것으로 서브 쿼리를 만들고, 메인 쿼리에서 해당 id를 in절로 걸어서 페이징을 수행한다.
   * 단, 서브 쿼리는 join이 없어야 한다.
   *
   * @param prefix : 메인 쿼리 페이징 대상 테이블 별칭
   * @param pk : 메인 쿼리 페이징 대상 테이블의 pk
   * @param tableName : 메인 쿼리 페이징 대상 테이블 이름
   * @param filter : 메인 쿼리 페이징 대상 테이블 필터링 조건
   * @param pageable : 메인 쿼리 페이징 조건
   */
  public static String getWhereClauseWithPagingBySubQuery(String prefix, @NonNull String pk, @NonNull String tableName, @NonNull Map<String, String> filter, Pageable pageable) {
    String resultQuery = """
        where pre-fixprimary-key IN (
            select temp.primary-key
            from (
                     select primary-key
                     from table-name table-alias
                     where-clause
                     order-paging-clause
                 ) temp
            )
            order-by
        """;
    return resultQuery.replace("pre-fix", prefix == null || prefix.isBlank() ? "" : prefix + "." )
        .replace("table-alias", prefix == null || prefix.isBlank() ? "" : prefix )
        .replace("primary-key",pk)
        .replace("table-name", tableName)
        .replace("where-clause", getSingleWhereClause(null, prefix, filter))
        .replace("order-paging-clause", pageable == null ? "" : PageableUtils.getSortAndLimit(pageable, prefix))
        .replace("order-by", pageable == null ? "" : PageableUtils.getSort(pageable, prefix));
  }
  public static String getPagingByReflect(@NonNull Class<?> clazz, @NonNull Map<String, String> filter, Pageable pageable) {
    R2dbcTable table = clazz.getAnnotation(R2dbcTable.class);
    /* 테이블명 획득 */
    String name = table.name();
    /* 별칭 획득 */
    String prefix = table.alias();
    /* pk값 획득 */
    Field[] fields = FieldUtils.getAllFields(clazz);
    String pk = null;
    for (Field field : fields) {
      if (field.isAnnotationPresent(Id.class)) {
        pk = R2oStringUtils.camelToSnake(field.getName());
        break;
      }
    }
    if (pk==null) throw new RuntimeException("테이블에 pk가 없습니다.");



    return getWhereClauseWithPagingBySubQuery(prefix, pk, name, filter, pageable);
  }

  public static String findByFilter(@NonNull Class<?> clazz, @NonNull Map<String, String> filter) {
    R2dbcTable table = clazz.getAnnotation(R2dbcTable.class);
    String prefix = table.alias();
    return getSingleWhereClause(null, prefix, filter);
  }

  /**
   * 테이블의 전체 레코드 수를 조회하는 쿼리를 만들어줌.
   * @param tableName : 테이블 이름
   * @param filter : 필터링 조건(columnName : value)
   */
  public static String getCountQuery(@NonNull String tableName, @NonNull Map<String, String> filter) {
    String resultQuery = """
        select count(*)
        from table-name
        where-clause
        """;
    return resultQuery.replace("table-name", tableName)
        .replace("where-clause", getSingleWhereClause(null, null, filter));
  }

  /**
   * 클래스의 필드를 조회해서 select절을 만들어줌.
   * 현재는 from, join절은 직접 구현해야함.
   * 차후에는 메소드 하나만 호출해도 join까지 다 되도록 하는 것이 목표.
   * 차차후에는 Object Mapping까지 되도록...
   *
   * @apiNote
   * 여러 테이블이 Join된다면, 아래처럼 재귀적인 형태로 호출.
   * ex) QueryUtils.getSingleSelectClause(RoleEntity.class, QueryUtils.getSingleSelectClause(RoleCategoryEntity.class, null,"cate"),"role")
   * return : SELECT role.selector as role_selector ... , cate.id as cate_id ...
   *
   * @param clazz : 조회할 클래스
   * @param existingSelectClause : 기존 select절. null이면 "SELECT "로 시작함.
   * @param tableAlias : 테이블 별칭. null이면 별칭 없이 칼럼명만 리턴함.
   * @return : SELECT 절을 리턴함.
   */
  @Deprecated
  public static String getSingleSelectClause(@NonNull Class<?> clazz, String existingSelectClause, String tableAlias) {
    Field[] allFields = FieldUtils.getAllFields(clazz);

    StringBuilder result = new StringBuilder(existingSelectClause==null ? "SELECT " : existingSelectClause + ", ");

    boolean tableAliasExist = tableAlias!=null && !tableAlias.isBlank();

    for (Field field : allFields) {
      /* Transient 걸려있다면 skip */
      if (field.isAnnotationPresent(Transient.class) && !field.isAnnotationPresent(R2dbcJoinColumn.class)) continue;
      /* JoinColumn 걸려있다면 skip TODO 이 부분역시 쿼리 만들어주는 함수로 변경해야함. */
      if (field.isAnnotationPresent(R2dbcJoinColumn.class)) {
        continue;
      }

      /* 테이블 별칭으로 프리픽스 생성. 테이블이 null이 아니거나, 비어있지 않을 경우 prefix 생성. */
      String prefix = tableAliasExist ? tableAlias+"." : "";

      /* 조회할 칼럼 생성 */
      Column annotation = field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class) : null;
      String column = annotation == null || annotation.value().isBlank() ? R2oStringUtils.camelToSnake(field.getName()) : annotation.value();

      /* 칼럼 별칭 추가 */
      String columnAlias = tableAliasExist ? " as " + tableAlias + "_" + column : "";


      /* 최종적인 칼럼 추가 */
      result.append(prefix).append(column).append(columnAlias).append(", ");
    }

    /* 결과물에서 마지막 ', ' 제거 */
    return result.substring(0, result.toString().length()-2) + " ";
  }


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
        new StringBuilder(" FROM " + mainTable.name() + " " + prefix + " "),
            null
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
          queryWrapper.getJoinQueue().add(
              JoinData
                  .builder()
                  .originAlias(joinTableAlias)
                  .targetEntity(targetEntity)
//                  .joinType(joinColumn.joinType())
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
              String relationTableAlias = R2oStringUtils.isBlank(manyToMany.relationTableAlias()) ? "relation_"+relationTableName : manyToMany.relationTableAlias();
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
      String column = annotation == null || annotation.value().isBlank() ? R2oStringUtils.camelToSnake(field.getName()) : annotation.value();

      /* 사용된 별칭 추가 및 최종적인 칼럼 추가 */
      if (!temp.endsWith(", ") && !temp.isBlank()) select.append(", ");
      select.append(createdPrefix).append(column).append(prefix.isBlank() ? "" : " as " + prefix + "_" + column);
    }

    /* 조인 큐를 확인하고, join을 수행한다. */
    while (!queryWrapper.getJoinQueue().isEmpty()) {
      JoinData poll = queryWrapper.getJoinQueue().poll();
      if (poll != null){
          getSingleSelectClauseWithJoin(poll.getTargetEntity(), queryWrapper, poll.getOriginAlias(), poll.isAlreadyOneToMany());
      }
    }

    return queryWrapper;
  }


  public static String getFindByIdWhereQuery(Class<?> clazz) {
    R2dbcTable mainTable = clazz.getAnnotation(R2dbcTable.class);
    String alias = mainTable.alias();
    Field[] totalFields = FieldUtils.getAllFields(clazz);
    String id = getId(totalFields);

    return " WHERE " + alias + "." + id + " = :id";
  }


  private static String getId(Field[] fields){
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
