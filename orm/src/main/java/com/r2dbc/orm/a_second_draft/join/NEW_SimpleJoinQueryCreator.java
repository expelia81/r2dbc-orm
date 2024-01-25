package com.r2dbc.orm.a_second_draft.join;

import com.r2dbc.orm.a_second_draft.annotations.*;
import com.r2dbc.orm.a_second_draft.query.ReoFieldUtils;
import com.r2dbc.orm.a_second_draft.utils.StringUtils;
import com.r2dbc.orm.first_draft.query.FieldUtils;
import com.r2dbc.orm.first_draft.query.QueryWrapper;
import com.r2dbc.orm.first_draft.query.R2dbcORM;
import lombok.NonNull;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.annotation.Transient;

import java.lang.reflect.Field;

public class NEW_SimpleJoinQueryCreator implements QueryCreator{

	public QueryWrapper createSelectQueryWithJoin(@NonNull Class<?> clazz) {

		R2dbcTable mainTable = AnnotationUtils.getAnnotation(clazz, R2dbcTable.class);

		QueryWrapper queryWrapper = QueryWrapper.create(mainTable);

		return this.createSelectQueryRecursive(clazz, queryWrapper, null, false);
	}

	/**
	 * 순환 참조 막기위해서, 모든 테이블의 별칭이 기록된다.
	 * 별칭은 빌드단계에서 고정되는 값이므로, 순환참조에서 무한한 반복은 방지된다.
	 */



	public QueryWrapper createSelectQueryRecursive(@NonNull Class<?> clazz, QueryWrapper query, String tableAlias, boolean alreadyOneToMany) {

		/*
		 * 흐름
		 * 1. 필드 순회
		 * 2. 필드 확인하고 연관관계 있을 경우 필드별 조인쿼리 추가할 수 있는 기능 따로 빼서 추가
		 * ----> ManyToOne, OneToMany, ManyToMany 따로 빼야만 한다.
		 * 3. 아닐 경우 필드를 쿼리에 추가.
		 *
		 * 이 과정은 Join이 발생하면 Queue에 쌓고, 마지막 단계에서 Queue에서 하나씩 꺼내며 재귀를 반복한다.
		 * 한 번 조회된 별칭의 도메인은 재귀를 수행하지 않는다.
		 */

		R2dbcTable mainTable = AnnotationUtils.getAnnotation(clazz, R2dbcTable.class);

		/* 이번 사이클에서 사용할 별칭을 선언한다. */
		String alias = StringUtils.isBlank(tableAlias) ? mainTable.alias() : tableAlias;

		Field[] allFields = ReoFieldUtils.getAllFields(clazz);

		for (Field field : allFields) {
			/* @Slf4j 사용시, log라는 필드가 생겨나므로, 무시해야한다.*/
			if (field.getName().equals("log") || field.getName().equals("logger")) continue;

			/* 필드가 @R2oTransient이면 무시한다. */
			if (field.isAnnotationPresent(R2oTransient.class)) continue;

			/* 여기부터는 ManyToOne 공통 메소드로 빠질 수 있다. */
			if (field.isAnnotationPresent(R2dbcManyToOne.class)) {
				ReoFieldUtils.manyToOne(query, field, alias, alreadyOneToMany, this);
			}

//			/* 여기부터는 OneToMany 공통 메소드로 빠질 수 있다. */
//			else if (field.isAnnotationPresent(R2dbcOneToMany.class)) ReoFieldUtils.oneToMany(query, field, alias, alreadyOneToMany);
//
//			/* 여기부터는 ManyToMany 공통 메소드로 빠질 수 있다. */
//			else if (field.isAnnotationPresent(R2dbcManyToMany.class)) ReoFieldUtils.manyToMany(query, field, alias, alreadyOneToMany);


			/* 여기부터는 필드를 쿼리에 추가하는 공통 메소드로 빠질 수 있다. */
			else {
				ReoFieldUtils.addSelectFieldQuery(query, field, alias);
			}




		}




		return query;
	}


}
