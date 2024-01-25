package com.r2dbc.orm.a_second_draft.join;

import com.r2dbc.orm.first_draft.query.QueryWrapper;
import lombok.NonNull;

public interface QueryCreator {

	QueryWrapper createSelectQueryWithJoin(@NonNull Class<?> clazz);

	QueryWrapper createSelectQueryRecursive(@NonNull Class<?> clazz, QueryWrapper query, String tableAlias, boolean alreadyOneToMany);


	}
