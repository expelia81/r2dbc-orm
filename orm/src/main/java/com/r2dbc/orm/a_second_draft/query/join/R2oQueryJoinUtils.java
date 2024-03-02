package com.r2dbc.orm.a_second_draft.query.join;

import com.r2dbc.orm.a_second_draft.annotations.R2dbcManyToOne;
import com.r2dbc.orm.a_second_draft.annotations.R2dbcOneToMany;
import com.r2dbc.orm.a_second_draft.exceptions.R2oMappingException;
import com.r2dbc.orm.a_second_draft.query.QueryWrapper;
import com.r2dbc.orm.a_second_draft.query.creator.QueryCreator;
import com.r2dbc.orm.a_second_draft.utils.R2oFieldUtils;
import com.r2dbc.orm.a_second_draft.utils.R2oStringUtils;
import com.r2dbc.orm.a_second_draft.utils.R2oTableUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;

public class R2oQueryJoinUtils {

	public static void manyToOne(QueryWrapper query, Field field, String originAlias
					, boolean alreadyOneToMany, QueryCreator queryCreator) {

		Class<?> type = field.getType();
		String alias = AnnotationUtils.getAnnotation(field, R2dbcManyToOne.class).alias();
		String joinTargetName = R2oTableUtils.getTableName(type);
		String joinTargetAlias = R2oStringUtils.isBlank(alias) ? R2oTableUtils.getTableAlias(type) : alias;

		if (query.isJoined(joinTargetAlias)) {
			return;
		} else {
			query.getQueriedAlias().add(joinTargetAlias);
		}

		String originColumnName = R2oFieldUtils.getJoinOwnColumnName(field);
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

		/* 해당 필드 조회문 추가 */
		R2oFieldUtils.addSelectFieldQuery(query, field, originAlias);
		queryCreator.createSelectQueryRecursive(type, query, joinTargetAlias, alreadyOneToMany);
	}

	public static void oneToMany(QueryWrapper query, Field field, String originAlias
					, boolean alreadyOneToMany, QueryCreator queryCreator) {

		R2dbcOneToMany oneToMany = AnnotationUtils.getAnnotation(field, R2dbcOneToMany.class);
		Class<?> type = oneToMany.targetEntity();
		if (type == Object.class) {
			throw new R2oMappingException("targetEntity is not defined in " + field.getName());
		}
		String alias = oneToMany.alias();

		String joinTargetName = R2oTableUtils.getTableName(type);
		String joinTargetAlias = R2oStringUtils.isBlank(alias) ? R2oTableUtils.getTableAlias(type) : alias;

		if (query.isJoined(joinTargetAlias)) {
			return;
		} else {
			query.getQueriedAlias().add(joinTargetAlias);
		}

		String originColumnName = R2oFieldUtils.getJoinOwnColumnName(field);
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

		/* 해당 필드 조회문 추가 */
//		R2oFieldUtils.addSelectFieldQuery(query, field, originAlias);
		queryCreator.createSelectQueryRecursive(type, query, joinTargetAlias, alreadyOneToMany);
	}

	public static void manyToMany(QueryWrapper query, Field field, String originAlias
					, boolean alreadyOneToMany, QueryCreator queryCreator) {
	}
}
