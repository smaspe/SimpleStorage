package com.njzk2.simplestorage;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.njzk2.simplestorage.handler.TypeHandler;

import android.provider.BaseColumns;

public class SQLSchema {
	private List<Entry<String, String>> fields = new ArrayList<Entry<String, String>>();

	private Class<? extends Storable> clazz;

	public SQLSchema(Class<? extends Storable> clazz) {
		this.clazz = clazz;
		addField(BaseColumns._ID, "integer primary key autoincrement");
		Field[] fields = SQLHelper.getFields(clazz);
		for (Field field : fields) {
			addField(field.getName(), TypeHandler.getHandler(field.getType())
					.getSQLType());
		}
	}

	private void addField(String fieldName, String fieldType) {
		fields.add(new AbstractMap.SimpleImmutableEntry<String, String>(
				fieldName, fieldType));
	}

	public String toString() {
		StringBuilder builder = new StringBuilder("create table ");
		builder.append(SQLHelper.getTableName(clazz));
		builder.append(" (");
		boolean first = true;
		for (Entry<String, String> field : fields) {
			if (first) {
				first = false;
			} else {
				builder.append(',');
			}
			builder.append(field.getKey());
			builder.append(' ');
			builder.append(field.getValue());
		}
		builder.append(");");
		return builder.toString();
	}
}