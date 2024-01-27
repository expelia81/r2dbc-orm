package com.r2dbc.orm.a_second_draft;

import com.r2dbc.orm.a_second_draft.query.creator.NEW_SimpleJoinQueryCreator;
import com.r2dbc.orm.a_second_draft.query.creator.QueryCreator;

public class QueryFactory {

	public static QueryCreator simple() {
		return new NEW_SimpleJoinQueryCreator();
	}

}
