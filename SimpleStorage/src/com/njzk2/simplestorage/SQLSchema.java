package com.njzk2.simplestorage;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.njzk2.simplestorage.handler.TypeHandler;

public class SQLSchema {
	private Map<String, TypeHandler> fields = new HashMap<String, TypeHandler>();

	private Class<? extends Storable> clazz;

	public SQLSchema(Class<? extends Storable> clazz) {
		this.clazz = clazz;
		Field[] fields = SQLHelper.getFields(clazz);
		for (Field field : fields) {
			addField(field.getName(), TypeHandler.getHandler(field.getType()));
		}
	}

	private void addField(String fieldName, TypeHandler fieldHandler) {
		fields.put(fieldName, fieldHandler);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder("create table ");
		builder.append(SQLHelper.getTableName(clazz));
		builder.append(" (id integer primary key autoincrement");
		for (Entry<String, TypeHandler> field : fields.entrySet()) {
			builder.append(',').append(field.getKey());
			builder.append(' ').append(field.getValue().getSQLType());
		}
		builder.append(");");
		return builder.toString();
	}
}