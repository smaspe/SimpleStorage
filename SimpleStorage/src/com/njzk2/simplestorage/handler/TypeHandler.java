package com.njzk2.simplestorage.handler;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;
import android.util.Log;

public abstract class TypeHandler<T> {

	private static Map<Type, TypeHandler<?>> HANDLERS = new HashMap<Type, TypeHandler<?>>();
	private static TypeHandler<String> DEFAULT_HANDLER = new StringHandler();
	static {
		HANDLERS.put(int.class, new IntHandler());
		HANDLERS.put(long.class, new LongHandler());
		HANDLERS.put(boolean.class, new BooleanHandler());
		HANDLERS.put(String.class, new StringHandler());
		HANDLERS.put(String[].class, new StringArrayHandler());
		HANDLERS.put(byte[].class, new BytesHandler());
		HANDLERS.put(Date.class, new DateHandler());
	}

	public static <C> void registerHandler(Class<C> clazz, TypeHandler<C> handler) {
		HANDLERS.put(clazz, handler);
	}

	public static void registerHandler(Type type, TypeHandler<?> handler) {
		// TODO how do I make sure the handler matches the type?
		Log.w("TypeHandler", "registering handler for " + type);
		HANDLERS.put(type, handler);
	}

	public static TypeHandler<?> getHandler(Field field) {
		Type type = field.getGenericType();
		if (HANDLERS.containsKey(type)) {
			return HANDLERS.get(type);
		}
		Log.w("TypeHandler", "No handler found for " + type.toString());
		System.out.println(HANDLERS);
		return DEFAULT_HANDLER;
	}

	public String getSQLType() {
		return "TEXT";
	}

	public Object getSQLValue(T object) {
		return object;
	}

	public abstract T fromCursor(String name, Cursor content);

	public static abstract class TypeToken<T> {
		// Freely inspired and simplifed from GSON TypeToken
		final private Type type;

		protected TypeToken() {
			this.type = getSuperclassTypeParameter(getClass());
		}

		static Type getSuperclassTypeParameter(Class<?> subclass) {
			Type superclass = subclass.getGenericSuperclass();
			if (superclass instanceof Class) {
				throw new RuntimeException("Missing type parameter.");
			}
			return ((ParameterizedType) superclass).getActualTypeArguments()[0];
		}

		public Type getType() {
			return type;
		}

	}
}
