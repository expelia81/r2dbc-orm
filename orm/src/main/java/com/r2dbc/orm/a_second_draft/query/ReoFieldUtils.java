package com.r2dbc.orm.a_second_draft.query;

import com.r2dbc.orm.a_second_draft.annotations.R2dbcManyToOne;
import com.r2dbc.orm.a_second_draft.annotations.R2dbcTable;
import com.r2dbc.orm.a_second_draft.join.QueryCreator;
import com.r2dbc.orm.a_second_draft.utils.StringUtils;
import com.r2dbc.orm.first_draft.query.QueryWrapper;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.relational.core.mapping.Column;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReoFieldUtils {

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

	public static String getColumnName(Field field) {
		if (field.isAnnotationPresent(Column.class)) {
			Column column = AnnotationUtils.getAnnotation(field, Column.class);
			return column.value();
		} else {
			return field.getName();
		}
	}


	public static Field[] getAllFields(Class<?> clazz) {

		return getFields(clazz, new ArrayList<>());
	}

	//TODO List 사용 배제, 배열만으로 동작하도록 해야함.
	private static Field[] getFields(Class<?> clazz, List<Field> result) {
		result.addAll(Arrays.asList(clazz.getDeclaredFields()));
		/* 슈퍼 클래스가 Object면 바로 리턴. */
		if (clazz.getSuperclass().equals(Object.class)) return result.toArray(new Field[0]);
		else {
			return getFields(clazz.getSuperclass(), result);
		}
	}

	public static QueryWrapper manyToOne(QueryWrapper query, Field field, String originAlias
					, boolean alreadyOneToMany, QueryCreator queryCreator) {

		/* 문법 에러 검사를 위한 임시 String 저장. TODO 필요한지??? */
		String temp = query.getSelect().toString();

		Class<?> type = field.getType();
		R2dbcManyToOne manyToOne = AnnotationUtils.getAnnotation(field, R2dbcManyToOne.class);
		R2dbcTable table = AnnotationUtils.getAnnotation(type, R2dbcTable.class);
		if (table == null) {
			throw new IllegalArgumentException("R2dbcTable annotation is not found in " + type.getName());
		}

		String targetAlias;

		String originColumnName = getColumnName(field);
		String targetColumnName = getJoinTargetColumnName(field);

		if (StringUtils.isBlank(manyToOne.alias())) {
			targetAlias = table.alias();
		} else {
			targetAlias = manyToOne.alias();
		}

		/* query 생성 */
		StringBuilder select = query.getSelect();
		StringBuilder from = query.getFrom();
		/* 조건절 생성 */
		from.append(" LEFT OUTER JOIN ")
						.append(table.name())
						.append(" ").append(targetAlias)
						.append(" ON ")
						.append(originAlias)
						.append(".")
						.append(originColumnName)
						.append(" = ")
						.append(targetAlias)
						.append(".")
						.append(targetColumnName);


//		/* temp */
//		if (!temp.endsWith(", ") && !temp.isBlank()) {
//			select.append(", ");
//		}
//		select.append(originAlias+"."+originColumnName + " as " + originAlias + "_" + originColumnName);
//
		/* 해당 필드 조회문 추가 */
		addSelectFieldQuery(query, field, originAlias);
		return queryCreator.createSelectQueryRecursive(type, query, targetAlias, alreadyOneToMany);
	}

	public static void addSelectFieldQuery(QueryWrapper query, Field field, String alias) {
		String columnName = getColumnName(field);
		query.getSelect().append(alias).append(".").append(columnName).append(" as ").append(alias).append("_").append(columnName).append(", ");
	}
}
