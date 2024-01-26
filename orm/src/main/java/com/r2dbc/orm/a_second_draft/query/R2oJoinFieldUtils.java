package com.r2dbc.orm.a_second_draft.query;

import com.r2dbc.orm.a_second_draft.annotations.R2dbcManyToOne;
import com.r2dbc.orm.a_second_draft.annotations.R2dbcTable;
import com.r2dbc.orm.a_second_draft.join.QueryCreator;
import com.r2dbc.orm.a_second_draft.utils.StringUtils;
import com.r2dbc.orm.first_draft.query.QueryWrapper;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;

public class R2oJoinFieldUtils {
	public static void manyToOne(QueryWrapper query, Field field, String originAlias
					, boolean alreadyOneToMany, QueryCreator queryCreator) {

		Class<?> type = field.getType();
		R2dbcManyToOne manyToOne = AnnotationUtils.getAnnotation(field, R2dbcManyToOne.class);

		String joinTargetName = R2oTableUtils.getTableName(type);
		String joinTargetAlias = StringUtils.isBlank(manyToOne.alias()) ? R2oTableUtils.getTableAlias(type) : manyToOne.alias();

		if (query.isJoined(joinTargetAlias)) {
			return;
		} else {
			query.getQueriedAlias().add(joinTargetAlias);
		}

		String originColumnName = R2oFieldUtils.getColumnName(field);
		String targetColumnName = R2oFieldUtils.getJoinTargetColumnName(field);


		/* 조건절 생성 */
		query.getFrom()
						.append(" LEFT OUTER JOIN ")
						.append(joinTargetName)
						.append(" ").append(joinTargetAlias)
						.append(" ON ")
						.append(originAlias)
						.append(".")
						.append(originColumnName)
						.append(" = ")
						.append(joinTargetAlias)
						.append(".")
						.append(targetColumnName);

//		/* temp */
//		if (!temp.endsWith(", ") && !temp.isBlank()) {
//			select.append(", ");
//		}
//		select.append(originAlias+"."+originColumnName + " as " + originAlias + "_" + originColumnName);
//
		/* 해당 필드 조회문 추가 */
		R2oFieldUtils.addSelectFieldQuery(query, field, originAlias);
		queryCreator.createSelectQueryRecursive(type, query, joinTargetAlias, alreadyOneToMany);
	}
}
