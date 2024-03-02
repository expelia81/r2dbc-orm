package com.r2dbc.orm.a_second_draft.map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.r2dbc.orm.a_second_draft.annotations.R2dbcManyToMany;
import com.r2dbc.orm.a_second_draft.annotations.R2dbcManyToOne;
import com.r2dbc.orm.a_second_draft.annotations.R2dbcOneToMany;
import com.r2dbc.orm.a_second_draft.map.join.Joiner;
import com.r2dbc.orm.a_second_draft.utils.*;
import io.r2dbc.spi.Readable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.annotation.Id;
import org.springframework.r2dbc.core.DatabaseClient;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class SimpleRelationMapper implements RelationMapper{

	Joiner joiner = null;

	SimpleRelationMapper() {
		joiner = new Joiner();
	}

	@Override
	public <T> T toEntity(Readable row, Class<T> entityClass, DatabaseClient client) {
		return mapRowToEntityWithJoin(entityClass, row, null, null, false, false);
	}

	private <T> T mapRowToEntityWithJoin(Class<T> clazz, Readable row, String joinAlias,
																			 List<String> alreadyMappedAlias, boolean stopJoin,
																			 boolean stopMultiJoin) {

//		R2dbcTable table = R2oReflectionUtils.getAnnotation(clazz, R2dbcTable.class);

		alreadyMappedAlias = Objects.requireNonNullElseGet(alreadyMappedAlias, ArrayList::new);

		T entity = R2oReflectionUtils.getEmptyEntity(clazz);

		/* 별칭이 지정된 상태로 시작되는 경우에는 입력된 별칭을, 그렇지 않다면 엔티티에 지정된 별칭을 가져간다. */
		String alias = R2oStringUtils.isBlank(joinAlias) ? R2oTableUtils.getTableAlias(clazz) : joinAlias;
		String prefix = alias.isBlank() ? "" : alias+"_";

		/* 최초 호출일 경우, 기본 엔티티의 별칭을 추가한다. */
		if (alreadyMappedAlias.isEmpty()) {
			log.info("add alias : " + alias);
			alreadyMappedAlias.add(alias);
		} else { // 이미 별칭이 있는 경우 중복체크를 수행한다.
			if(alreadyMappedAlias.contains(alias)) {
				log.error("Already mapped alias : " + alias);
				return null;
			} else {
				alreadyMappedAlias.add(alias);
			}
		}

		Field[] fields = R2oFieldUtils.getAllFields(clazz);
		Object value;

		for (Field field : fields) {
			if(R2oFieldUtils.isNotTargetField(field)) continue;



			if (R2oFieldUtils.isJoinColumn(field)) {
				if (stopJoin) continue;
				if (field.isAnnotationPresent(R2dbcManyToOne.class)) {
					/* ManyToOne */
					R2dbcManyToOne manyToOne = AnnotationUtils.findAnnotation(field,R2dbcManyToOne.class);
					Class<?> targetEntity = field.getType();
					String targetAlias = manyToOne.alias();
					value = mapRowToEntityWithJoin(targetEntity, row, targetAlias, alreadyMappedAlias, targetEntity.equals(clazz), true);
					FieldUtils.setField(entity, field, value);
				} else {
					/* TODO can multiple OneToMany, ManyToMany ... */
					if (stopMultiJoin) continue;
					/* OneToMany, ManyToMany */
					if (field.isAnnotationPresent(R2dbcOneToMany.class)) {
						/* OneToMany */
						R2dbcOneToMany oneToMany = AnnotationUtils.findAnnotation(field,R2dbcOneToMany.class);
						Class<?> targetEntity = Objects.requireNonNull(oneToMany).targetEntity();
						String targetAlias = R2oStringUtils.isBlank(oneToMany.alias()) ? R2oTableUtils.getTableAlias(targetEntity) : oneToMany.alias();
						Object target = mapRowToEntityWithJoin(targetEntity, row, targetAlias, alreadyMappedAlias, targetAlias.equals(alias), true);
						value = target!=null ? List.of(target) : List.of();
					} else {
						/* ManyToMany */
						R2dbcManyToMany manyToMany = AnnotationUtils.findAnnotation(field,R2dbcManyToMany.class);
						Class<?> targetEntity = Objects.requireNonNull(manyToMany).targetManyEntity();
						String targetAlias = manyToMany.relationTableAlias();
						Object target = mapRowToEntityWithJoin(targetEntity, row, targetAlias, alreadyMappedAlias, targetAlias.equals(alias), true);
						value = target!=null ? List.of(target) : List.of();
					}
					FieldUtils.setField(entity, field, value);
					continue;
				}
			} else {
				String columnName = R2oFieldUtils.getColumnName(field);
				value = row.get(prefix + columnName, field.getType());
				if (value == null && field.isAnnotationPresent(Id.class)) {
					return null;
				}
				if (value != null){
					FieldUtils.setField(entity, field, value);
				}
			}
		}

		return entity;
	}




}




















