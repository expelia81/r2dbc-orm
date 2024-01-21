package com.r2dbc.orm.a_second_draft.join;

import com.r2dbc.orm.first_draft.query.QueryWrapper;
import lombok.NonNull;

public interface JoinQueryCreator {

	QueryWrapper createSelectClauseWithJoin(@NonNull Class<?> clazz, QueryWrapper existingQuery, String tableAlias, boolean alreadyOneToMany);

}
