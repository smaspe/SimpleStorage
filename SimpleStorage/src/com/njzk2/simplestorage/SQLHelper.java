package com.njzk2.simplestorage;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SQLHelper {
	private static Map<Class<?>, Field[]> fields = new HashMap<Class<?>, Field[]>();

	static Field[] getFields(Class<? extends Storable> clazz) {
		if (!fields.containsKey(clazz)) {
			fields.put(clazz, clazz.getDeclaredFields());
		}
		return fields.get(clazz);
	}

	public static String getTableName(Class<? extends Storable> clazz) {
		return clazz.getSimpleName();
	}
}
