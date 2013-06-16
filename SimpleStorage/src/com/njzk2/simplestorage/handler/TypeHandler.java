package com.njzk2.simplestorage.handler;

import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;

public class TypeHandler {

	private static Map<Class<?>, TypeHandler> HANDLERS = new HashMap<Class<?>, TypeHandler>();
	private static TypeHandler DEFAULT_HANDLER = new TypeHandler();
	static {
		HANDLERS.put(int.class, new IntHandler());
	}
	public static TypeHandler getHandler(Class<?> type) {
		if (HANDLERS.containsKey(type)) {
			return HANDLERS.get(type);
		}
		return DEFAULT_HANDLER;
	}

	public String getSQLType() {
		return "TEXT";
	}

	public Object getSQLValue(Object object) {
		return object;
	}

	public Object fromCursor(String name, Cursor content) {
		return content.getString(content.getColumnIndex(name));
	}
}
