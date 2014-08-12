package com.njzk2.simplestorage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.njzk2.simplestorage.handler.TypeHandler;

public class SQLSchema {
	private Map<String, TypeHandler<?>> fields = new HashMap<String, TypeHandler<?>>();

	private Class<? extends Storable> clazz;

	public SQLSchema(Class<? extends Storable> clazz) {
		this.clazz = clazz;
		Field[] clazzFields = SQLHelper.getFields(clazz);
		for (Field field : clazzFields) {
			addField(field.getName(), TypeHandler.getHandler(field));
		}
	}

	private void addField(String fieldName, TypeHandler<?> fieldHandler) {
		fields.put(fieldName, fieldHandler);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder("create table if not exists ");
		builder.append(SQLHelper.getTableName(clazz));
		builder.append(" (_id integer primary key autoincrement");
		for (Entry<String, TypeHandler<?>> field : fields.entrySet()) {
			builder.append(',').append(field.getKey());
			builder.append(' ').append(field.getValue().getSQLType());
		}
		builder.append(");");
		return builder.toString();
	}

	public Collection<String> toAlterStrings(Collection<String> columns) {
		Collection<String> alterStrings = new ArrayList<String>();
		for (Entry<String, TypeHandler<?>> field : fields.entrySet()) {
			if (columns.contains(field.getKey())) {
				// It is not possible to alter an existing column
				continue;
			}
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("alter table ").append(SQLHelper.getTableName(clazz)).append(" add column ");
			stringBuilder.append(field.getKey()).append(' ').append(field.getValue().getSQLType());
			alterStrings.add(stringBuilder.toString());
		}
		return alterStrings;
	}
}