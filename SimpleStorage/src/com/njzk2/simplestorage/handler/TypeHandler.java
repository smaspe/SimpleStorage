package com.njzk2.simplestorage.handler;

import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;
import android.util.Log;

public abstract class TypeHandler {

	private static Map<Class<?>, TypeHandler> HANDLERS = new HashMap<Class<?>, TypeHandler>();
	private static TypeHandler DEFAULT_HANDLER = new StringHandler();
	static {
		HANDLERS.put(int.class, new IntHandler());
		HANDLERS.put(long.class, new LongHandler());
		HANDLERS.put(boolean.class, new BooleanHandler());
		HANDLERS.put(String.class, new StringHandler());
		HANDLERS.put(byte[].class, new BytesHandler());
	}

	public static TypeHandler getHandler(Class<?> type) {
		if (HANDLERS.containsKey(type)) {
			return HANDLERS.get(type);
		}
		Log.w("TypeHandler", "No handler found for " + type.getCanonicalName());
		return DEFAULT_HANDLER;
	}

	public String getSQLType() {
		return "TEXT";
	}

	public Object getSQLValue(Object object) {
		return object;
	}

	public abstract Object fromCursor(String name, Cursor content);
}
