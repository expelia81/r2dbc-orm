package com.r2dbc.orm.a_second_draft.utils;

import com.r2dbc.orm.a_second_draft.annotations.R2dbcTable;
import com.r2dbc.orm.a_second_draft.exceptions.R2oMappingException;
import org.springframework.core.annotation.AnnotationUtils;

public class R2oTableUtils {


	public static String getTableAlias(Class<?> entityClass) {
		R2dbcTable table = AnnotationUtils.getAnnotation(entityClass, R2dbcTable.class);
		if (table == null) throw new R2oMappingException("R2dbcTable annotation is not found in " + entityClass.getSimpleName());

		String alias = table.alias();
		String name = table.name();
		String entityName = entityClass.getSimpleName().toLowerCase();

		if(!R2oStringUtils.isBlank(alias)) return alias;
		else if(!R2oStringUtils.isBlank(name)) return name;
		else return entityName;
	}

	public static String getTableName(Class<?> entityClass) {
		R2dbcTable table = AnnotationUtils.getAnnotation(entityClass, R2dbcTable.class);
		if (table == null) throw new R2oMappingException("R2dbcTable annotation is not found in " + entityClass.getSimpleName());

		String name = table.name();
		String entityName = entityClass.getSimpleName().toUpperCase();

		if(!R2oStringUtils.isBlank(name)) return name;
		else return entityName;
	}
}
