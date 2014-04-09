package com.njzk2.simplestorage;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SQLHelper {
	private static Map<Class<?>, Field[]> fields = new HashMap<Class<?>, Field[]>();

	static Field[] getFields(Class<? extends Storable> clazz) {
		if (!fields.containsKey(clazz)) {
			Collection<Field> fs = new ArrayList<Field>();
			for (Field field : clazz.getDeclaredFields()) {
				if (!Modifier.isStatic(field.getModifiers())) {
					fs.add(field);
				}
			}
			fields.put(clazz, fs.toArray(new Field[0]));
		}
		return fields.get(clazz);
	}

	public static String getTableName(Class<? extends Storable> clazz) {
		return clazz.getSimpleName();
	}
}
