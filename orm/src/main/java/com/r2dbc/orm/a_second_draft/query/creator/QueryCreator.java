package com.r2dbc.orm.a_second_draft.query.creator;

import com.r2dbc.orm.a_second_draft.query.QueryWrapper;
import lombok.NonNull;

public interface QueryCreator {

	QueryWrapper createSelectQueryWithJoin(@NonNull Class<?> clazz);

	QueryWrapper createSelectQueryRecursive(@NonNull Class<?> clazz, QueryWrapper query, String tableAlias, boolean alreadyOneToMany);


	}
