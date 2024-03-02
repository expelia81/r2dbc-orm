package com.r2dbc.orm.a_second_draft.query.creator;

import com.r2dbc.orm.a_second_draft.annotations.R2dbcManyToMany;
import com.r2dbc.orm.a_second_draft.annotations.R2dbcManyToOne;
import com.r2dbc.orm.a_second_draft.annotations.R2dbcOneToMany;
import com.r2dbc.orm.a_second_draft.annotations.R2oTransient;
import com.r2dbc.orm.a_second_draft.utils.R2oFieldUtils;
import com.r2dbc.orm.a_second_draft.utils.R2oTableUtils;
import com.r2dbc.orm.a_second_draft.query.QueryWrapper;
import com.r2dbc.orm.a_second_draft.query.join.JoinData;
import com.r2dbc.orm.a_second_draft.query.join.R2oJoinType;
import com.r2dbc.orm.a_second_draft.utils.R2oStringUtils;
import lombok.NonNull;

import java.lang.reflect.Field;

public class NEW_SimpleJoinQueryCreator implements QueryCreator {

	public QueryWrapper createSelectQueryWithJoin(@NonNull Class<?> clazz) {

		QueryWrapper query = QueryWrapper.create(clazz, this);

		this.createSelectQueryRecursive(clazz, query, null, false);

		/* JoinQueue에 쌓인 조인을 수행한다. */
		// alreadyOneToMany가 true인 경우에는 더이상 oneToMany를 수행하지 않는다.
		// TODO simple, statdard, advanced, full에 따라 다르게 동작하게 구현을 변경해야한다.
//		query.getJoinQueue()
//						.forEach(joinData -> joinData.join(query, this));
		while (!query.getJoinQueue().isEmpty()) {
			JoinData poll = query.getJoinQueue().poll();
			if (poll != null){
				poll.join(query, this);
			}
		}



		return query;
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
		 *
		 * alreadyOneToMany가 참조형이 아닌 이유
		 * -> 재귀를 돌며 같은 레이어에 있는 OneToMany
		 */

		/* 이번 사이클에서 사용할 별칭을 선언한다. */
		String alias = R2oStringUtils.isBlank(tableAlias) ? R2oTableUtils.getTableAlias(clazz) : tableAlias;

		Field[] allFields = R2oFieldUtils.getAllFields(clazz);

		for (Field field : allFields) {
			/* @Slf4j 사용시, log라는 필드가 생겨나므로, 무시해야한다.*/
			if (field.getName().equals("log") || field.getName().equals("logger")) continue;

			/* 필드가 @R2oTransient이면 무시한다. */
			if (field.isAnnotationPresent(R2oTransient.class)) continue;

			/* 여기부터는 ManyToOne 공통 메소드로 빠질 수 있다.
			 * TODO JoinLevel에 따라서 다른 행동을 보장받을 수 있어야한다. */
			if (field.isAnnotationPresent(R2dbcManyToOne.class)) {
				R2oFieldUtils.addSelectFieldQuery(query, field, alias);
				query.getJoinQueue().add(JoinData.create(field.getType(), field, alias, R2oJoinType.MANY_TO_ONE, alreadyOneToMany));
				/* 여기부터는 OneToMany 공통 메소드로 빠질 수 있다. TODO oneToMany는 already반영을 잊으면 안된다. */
			} else if (field.isAnnotationPresent(R2dbcOneToMany.class)) {
				query.getJoinQueue().add(JoinData.create(field.getType(), field, alias, R2oJoinType.ONE_TO_MANY, true));
				/* 여기부터는 ManyToMany 공통 메소드로 빠질 수 있다. */
			}	else if (field.isAnnotationPresent(R2dbcManyToMany.class)) {
				query.getJoinQueue().add(JoinData.create(field.getType(), field, alias, R2oJoinType.MANY_TO_MANY, true));
			} else {
				R2oFieldUtils.addSelectFieldQuery(query, field, alias);
			}

		}

		return query;
	}


}
