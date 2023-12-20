package com.r2dbc.orm.first_draft.query;

import com.r2dbc.orm.annotations.R2dbcJoinColumn;
import lombok.Builder;
import lombok.Getter;

/**
 * JoinQueue를 쓰는 이유는, 중복된 alias 사용을 체크하게 해주며, 중복된 alias 사용으로 인해 쿼리가 중단되는 대신, joinQueue에 쌓아두고, 쿼리가 끝난 후에 join쿼리를 추가하기 위함이다.
 */
@Getter
@Builder
public class JoinData {
  private Class<?> targetEntity;
  private String alias;
  private R2dbcJoinColumn.JoinType joinType;
  private boolean alreadyOneToMany;
}

