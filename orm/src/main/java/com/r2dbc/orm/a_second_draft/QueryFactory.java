package com.r2dbc.orm.a_second_draft;

import com.r2dbc.orm.a_second_draft.join.NEW_SimpleJoinQueryCreator;
import com.r2dbc.orm.a_second_draft.join.QueryCreator;

public class QueryFactory {

	public static QueryCreator simple() {
		return new NEW_SimpleJoinQueryCreator();
	}

}
