package com.r2dbc.orm.a_second_draft.query.join;

import com.r2dbc.orm.a_second_draft.annotations.R2dbcManyToOne;
import com.r2dbc.orm.a_second_draft.query.QueryWrapper;
import com.r2dbc.orm.a_second_draft.query.R2oJoinFieldUtils;
import com.r2dbc.orm.a_second_draft.query.creator.QueryCreator;
import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Field;

/**
 * JoinQueue를 쓰는 이유는, 중복된 alias 사용을 체크하게 해주며, 중복된 alias 사용으로 인해 쿼리가 중단되는 대신, joinQueue에 쌓아두고, 쿼리가 끝난 후에 join쿼리를 추가하기 위함이다.
 */
@Getter
@Builder
public class JoinData {
  private Class<?> targetEntity;
  private Field field;
  private String originAlias;
  private R2oJoinType joinType;
  private boolean alreadyOneToMany;

  public static JoinData create(Class<?> targetEntity, Field field, String originAlias, R2oJoinType joinType, boolean alreadyOneToMany) {
    return JoinData.builder()
            .targetEntity(targetEntity)
            .field(field)
            .originAlias(originAlias)
            .joinType(joinType)
            .alreadyOneToMany(alreadyOneToMany)
            .build();
  }

  public void join(QueryWrapper query, QueryCreator queryCreator) {

    switch (joinType){
      case MANY_TO_ONE:
        R2oJoinFieldUtils.manyToOne(query, field, originAlias, alreadyOneToMany, queryCreator);
        break;
      case ONE_TO_MANY:
//        R2oJoinFieldUtils.oneToMany(query, field, originAlias, alreadyOneToMany, queryCreator);
        break;
      case MANY_TO_MANY:
//        R2oJoinFieldUtils.manyToMany(query, field, originAlias, alreadyOneToMany, queryCreator);
        break;
      default:
        throw new IllegalArgumentException("joinType is not valid");
    }

  }
}

