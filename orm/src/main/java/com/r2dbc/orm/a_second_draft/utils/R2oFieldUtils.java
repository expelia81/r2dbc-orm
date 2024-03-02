package com.r2dbc.orm.a_second_draft.utils;

import com.r2dbc.orm.a_second_draft.annotations.R2dbcManyToMany;
import com.r2dbc.orm.a_second_draft.annotations.R2dbcManyToOne;
import com.r2dbc.orm.a_second_draft.annotations.R2dbcOneToMany;
import com.r2dbc.orm.a_second_draft.annotations.R2oTransient;
import com.r2dbc.orm.a_second_draft.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Slf4j
public class R2oFieldUtils {

	public static String getJoinTargetColumnName(Field field) {

		if (field.isAnnotationPresent(R2dbcManyToOne.class)) {
			R2dbcManyToOne manyToOne = AnnotationUtils.getAnnotation(field, R2dbcManyToOne.class);
			return manyToOne.targetColumnName();
		} else if (field.isAnnotationPresent(R2dbcManyToMany.class)) {
			R2dbcManyToMany manyToMany = AnnotationUtils.getAnnotation(field, R2dbcManyToMany.class);
			return manyToMany.manyColumnName();
		} else if (field.isAnnotationPresent(R2dbcOneToMany.class)) {
			R2dbcOneToMany oneToMany = AnnotationUtils.getAnnotation(field, R2dbcOneToMany.class);
			return oneToMany.targetColumn();
		} else {
			return getColumnName(field);
		}
	}
	public static String getJoinOwnColumnName(Field field) {
		if (field.isAnnotationPresent(R2dbcManyToOne.class)) {
			R2dbcManyToOne manyToOne = AnnotationUtils.getAnnotation(field, R2dbcManyToOne.class);
			return manyToOne.name();
		} else if (field.isAnnotationPresent(R2dbcManyToMany.class)) {
			R2dbcManyToMany manyToMany = AnnotationUtils.getAnnotation(field, R2dbcManyToMany.class);
			return manyToMany.oneColumnName();
		} else if (field.isAnnotationPresent(R2dbcOneToMany.class)) {
			R2dbcOneToMany oneToMany = AnnotationUtils.getAnnotation(field, R2dbcOneToMany.class);
			return oneToMany.name();
		} else {
			return getColumnName(field);
		}
	}

	public static boolean isJoinColumn(Field field) {
		return field.isAnnotationPresent(R2dbcOneToMany.class)
						|| field.isAnnotationPresent(R2dbcManyToOne.class)
						|| field.isAnnotationPresent(R2dbcManyToMany.class);
	}

	public static String getColumnName(Field field) {

		String name;

		if (field.isAnnotationPresent(R2dbcManyToOne.class)) {
			R2dbcManyToOne manyToOne = AnnotationUtils.getAnnotation(field, R2dbcManyToOne.class);
			name = R2oStringUtils.isBlank(manyToOne.name()) ? field.getName() : manyToOne.name();
		} else if (field.isAnnotationPresent(Column.class)) {
			Column column = AnnotationUtils.getAnnotation(field, Column.class);
			name = R2oStringUtils.isBlank(column.value()) ? field.getName() : column.value();
		} else {
			name = field.getName();
		}

		return R2oStringUtils.camelToSnake(name);
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

	/**
	 * true이면????????????????????????
	 * @param field
	 * @return
	 */
	public static boolean isNotTargetField(Field field) {

		/* slf4j 사용시 넘어간다. */
		if (field.getName().equals("log") || field.getName().equals("logger")) return true;
		if (field.isAnnotationPresent(R2oTransient.class)) return true;
		/* transient 필드는 무시되나, R2dbcJoinColumn과 같이 쓰면 무시되지 않는다. */
		if (field.isAnnotationPresent(Transient.class)) {
			return !R2oFieldUtils.isJoinColumn(field);
		}
		return false;
	}

//	public static void parseField(Field field, Class<?> clazz, String originAlias, boolean alreadyOneToMany, QueryWrapper query, QueryCreator queryCreator) {
//		//TODO
//		if (field.isAnnotationPresent(R2dbcManyToOne.class)) {
//			R2oFieldUtils.addSelectFieldQuery(query, field, originAlias);
//			query.getJoinQueue().add(JoinData.create(field.getType(), field, originAlias, R2oJoinType.MANY_TO_ONE, alreadyOneToMany));
//			/* 여기부터는 OneToMany 공통 메소드로 빠질 수 있다. TODO oneToMany는 already반영을 잊으면 안된다. */
//		} else if (field.isAnnotationPresent(R2dbcOneToMany.class)) {
//			query.getJoinQueue().add(JoinData.create(field.getType(), field, originAlias, R2oJoinType.ONE_TO_MANY, true));
//			/* 여기부터는 ManyToMany 공통 메소드로 빠질 수 있다. */
//		}	else if (field.isAnnotationPresent(R2dbcManyToMany.class)) {
//			query.getJoinQueue().add(JoinData.create(field.getType(), field, originAlias, R2oJoinType.MANY_TO_MANY, true));
//		}
//		/* 여기부터는 필드를 쿼리에 추가하는 공통 메소드로 빠질 수 있다. */
//		else {
//			R2oFieldUtils.addSelectFieldQuery(query, field, originAlias);
//		}
//	}
}
