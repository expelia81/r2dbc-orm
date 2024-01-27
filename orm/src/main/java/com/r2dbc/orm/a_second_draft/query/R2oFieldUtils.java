package com.r2dbc.orm.a_second_draft.query;

import com.r2dbc.orm.a_second_draft.annotations.R2dbcManyToOne;
import com.r2dbc.orm.a_second_draft.query.creator.QueryCreator;
import com.r2dbc.orm.a_second_draft.query.join.JoinData;
import com.r2dbc.orm.a_second_draft.query.join.R2oJoinType;
import com.r2dbc.orm.a_second_draft.utils.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.relational.core.mapping.Column;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class R2oFieldUtils {

	public static String getJoinTargetColumnName(Field field) {

		if (field.isAnnotationPresent(R2dbcManyToOne.class)) {
			R2dbcManyToOne manyToOne = AnnotationUtils.getAnnotation(field, R2dbcManyToOne.class);
			return manyToOne.targetColumnName();
//		} else if (field.isAnnotationPresent(R2dbcManyToMany.class)) {
//			R2dbcManyToMany manyToMany = AnnotationUtils.getAnnotation(field, R2dbcManyToMany.class);
//			return manyToMany.targetColumnName();
//		} else if (field.isAnnotationPresent(R2dbcOneToMany.class)) {
//			R2dbcOneToMany oneToMany = AnnotationUtils.getAnnotation(field, R2dbcOneToMany.class);
//			return oneToMany.targetColumnName();
		} else {
			return field.getName();
		}
	}

	public static String getColumnNameSnake(Field field) {
		return StringUtils.camelToSnake(getColumnName(field));
	}

	public static String getColumnName(Field field) {

		String name;

		if (field.isAnnotationPresent(R2dbcManyToOne.class)) {
			R2dbcManyToOne manyToOne = AnnotationUtils.getAnnotation(field, R2dbcManyToOne.class);
			name = StringUtils.isBlank(manyToOne.name()) ? field.getName() : manyToOne.name();
		} else if (field.isAnnotationPresent(Column.class)) {
			Column column = AnnotationUtils.getAnnotation(field, Column.class);
			name = StringUtils.isBlank(column.value()) ? field.getName() : column.value();
		} else {
			name = field.getName();
		}

		return StringUtils.camelToSnake(name);
	}


	public static Field[] getAllFields(Class<?> clazz) {

		return getFields(clazz, new ArrayList<>());
	}

	private static Field[] getFields(Class<?> clazz, List<Field> result) {
		result.addAll(Arrays.asList(clazz.getDeclaredFields()));
		/* 슈퍼 클래스가 Object면 바로 리턴. */
		if (clazz.getSuperclass().equals(Object.class)) return result.toArray(new Field[0]);
		else {
			return getFields(clazz.getSuperclass(), result);
		}
	}

	public static void addSelectFieldQuery(QueryWrapper query, Field field, String alias) {
		String columnName = getColumnName(field);
		query.getSelect()
						.append(alias)
						.append(".")
						.append(columnName)
						.append(" as ")
						.append(alias)
						.append("_")
						.append(columnName)
						.append(", ");
	}

	public static void parseField(Field field, Class<?> clazz, String originAlias, boolean alreadyOneToMany, QueryWrapper query, QueryCreator queryCreator) {
		//TODO
		if (field.isAnnotationPresent(R2dbcManyToOne.class)) {
			R2oFieldUtils.addSelectFieldQuery(query, field, originAlias);
			query.getJoinQueue().add(JoinData.create(field.getType(), field, originAlias, R2oJoinType.MANY_TO_ONE, alreadyOneToMany));


//			/* 여기부터는 OneToMany 공통 메소드로 빠질 수 있다. TODO oneToMany는 already반영을 잊으면 안된다. */
//		} else if (field.isAnnotationPresent(R2dbcOneToMany.class)) {
//			R2oJoinFieldUtils.oneToMany(query, field, originAlias, false, queryCreator);
//			/* 여기부터는 ManyToMany 공통 메소드로 빠질 수 있다. */
//		}	else if (field.isAnnotationPresent(R2dbcManyToMany.class)) {
//			R2oJoinFieldUtils.manyToMany(query, field, originAlias, false, queryCreator);
		}


		/* 여기부터는 필드를 쿼리에 추가하는 공통 메소드로 빠질 수 있다. */
		else {
			R2oFieldUtils.addSelectFieldQuery(query, field, originAlias);
		}
	}
}
