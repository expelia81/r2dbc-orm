package com.r2dbc.orm.a_second_draft.query;

import com.r2dbc.orm.a_second_draft.annotations.R2dbcTable;
import com.r2dbc.orm.a_second_draft.utils.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

public class R2oTableUtils {


	public static String getTableAlias(Class<?> entityClass) {
		R2dbcTable table = AnnotationUtils.getAnnotation(entityClass, R2dbcTable.class);
		if (table == null) throw new IllegalArgumentException("R2dbcTable annotation is not found in " + table.getClass().getName());

		String alias = table.alias();
		String name = table.name();
		String entityName = entityClass.getSimpleName().toLowerCase();

		if(!StringUtils.isBlank(alias)) return alias;
		else if(!StringUtils.isBlank(name)) return name;
		else return entityName;
	}

	public static String getTableName(Class<?> entityClass) {
		R2dbcTable table = AnnotationUtils.getAnnotation(entityClass, R2dbcTable.class);
		if (table == null) throw new IllegalArgumentException("R2dbcTable annotation is not found in " + table.getClass().getName());

		String name = table.name();
		String entityName = entityClass.getSimpleName().toUpperCase();

		if(!StringUtils.isBlank(name)) return name;
		else return entityName;
	}
}
