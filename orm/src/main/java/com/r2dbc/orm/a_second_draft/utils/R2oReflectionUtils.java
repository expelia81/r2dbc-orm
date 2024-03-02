package com.r2dbc.orm.a_second_draft.utils;

import com.r2dbc.orm.a_second_draft.annotations.R2dbcManyToMany;
import com.r2dbc.orm.a_second_draft.annotations.R2dbcManyToOne;
import com.r2dbc.orm.a_second_draft.annotations.R2dbcOneToMany;
import com.r2dbc.orm.a_second_draft.exceptions.R2oMappingException;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class R2oReflectionUtils {
	public static <T> T getEmptyEntity(Class<T> clazz) {
		T entity;
		try {
			entity = clazz.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new R2oMappingException(clazz.getSimpleName() + " : No default constructor found.");
		}
		return entity;
	}

	public static <A extends Annotation> A getAnnotation(Class<?> clazz, Class<A> annotationType) {
		A anno = AnnotationUtils.findAnnotation(clazz, annotationType);
		if (anno == null) {
			throw new R2oMappingException(clazz.getSimpleName() + " : "+ annotationType.getSimpleName()+" annotation is not found.");
		}
		return anno;
	}
}
