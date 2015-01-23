package com.njzk2.simplestorage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Parcel;
import android.provider.BaseColumns;
import android.util.Log;

import com.njzk2.simplestorage.handler.TypeHandler;

public class Storable {

	private static final String BY_ID = BaseColumns._ID + "= ?";
	private long id = -1;

	public final long getRowId() {
		return id;
	}

	public void save(Context context) {
		Map<String, Object> mapRepr = asMap();
		Parcel p = Parcel.obtain();
		p.writeMap(mapRepr);
		p.setDataPosition(0);
		ContentValues values = ContentValues.CREATOR.createFromParcel(p);
		p.recycle();
		SQLiteDatabase database = Database.getInstance(context).getWritableDatabase();
		if (id == -1) {
			id = database.insert(SQLHelper.getTableName(getClass()), null, values);
		} else {
			database.update(SQLHelper.getTableName(getClass()), values, BY_ID,
					new String[] { String.valueOf(id) });
			// TODO test return value and log error or something
		}
	}

	public boolean delete(Context context) {
		boolean result = Database.getInstance(context).getWritableDatabase()
				.delete(SQLHelper.getTableName(getClass()), BY_ID, new String[] { String.valueOf(id) }) > 0;
		if (result) {
			id = -1;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> asMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		Field[] fields = SQLHelper.getFields(getClass());
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				TypeHandler<Object> handler = (TypeHandler<Object>) TypeHandler.getHandler(field);
				res.put(field.getName(), handler.getSQLValue(field.get(this)));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	protected void loadCursor(Cursor content) {
		id = content.getLong(content.getColumnIndex("_id"));
		Field[] fields = SQLHelper.getFields(getClass());
		for (Field field : fields) {
			TypeHandler<?> handler = TypeHandler.getHandler(field);
			try {
				field.setAccessible(true);
				field.set(this, handler.fromCursor(field.getName(), content));
			} catch (Exception e) {
				Log.e(getClass().getSimpleName(), "Exception with field " + field);
				e.printStackTrace();
			}
		}
	}

	public static int delete(Context context, Class<? extends Storable> clazz) {
		return Database.getInstance(context).getWritableDatabase().delete(SQLHelper.getTableName(clazz), null, null);
	}

	public static <T extends Storable> T getById(Context context,
			Class<T> clazz, long id) {
		try {
			T result = clazz.newInstance();
			Cursor content = list(context, clazz, BY_ID, String.valueOf(id));
			if (content.moveToFirst()) {
				result.loadCursor(content);
				content.close();
				return result;
			} else {
				content.close();
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T extends Storable> T getBy(Context context,
			Class<T> clazz, String where, String... params) {
		try {
			T result = clazz.newInstance();

			Cursor content = list(context, clazz, where, params);
			if (content.moveToFirst()) {
				result.loadCursor(content);
				content.close();
				return result;
			} else {
				content.close();
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T extends Storable> List<T> findBy(Context context,
			Class<T> clazz, String where, String... params) {
		List<T> res = new ArrayList<T>();
		try {
			Cursor content = list(context, clazz, where, params);
			while (content.moveToNext()) {
				T result = clazz.newInstance();
				result.loadCursor(content);
				res.add(result);
			}
			content.close();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static Uri getPath(Class<? extends Storable> clazz) {
		return Uri.withAppendedPath(DataProvider.DATA_CONTENT_URI,
				SQLHelper.getTableName(clazz));
	}

	public static Uri getPath(Class<? extends Storable> clazz, long id) {
		return Uri.withAppendedPath(
				Uri.withAppendedPath(DataProvider.DATA_CONTENT_URI,
						SQLHelper.getTableName(clazz)), String.valueOf(id));
	}

	public static Cursor list(Context context, Class<? extends Storable> clazz, String where, String... params) {
		return Database.getInstance(context).getWritableDatabase()
				.query(SQLHelper.getTableName(clazz), null, where, params, null, null, null);
	}
}
