package com.njzk2.simplestorage;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.njzk2.simplestorage.handler.TypeHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;

public class Storable {

	private long id = -1;
	public final long getId() {
		return id;
	}

	public void save(Context context) {
		Map<String, Object> mapRepr = asMap();
		Parcel p = Parcel.obtain();
		p.writeMap(mapRepr);
		p.setDataPosition(0);
		ContentValues values = ContentValues.CREATOR.createFromParcel(p);
		if (id == -1) {
			Uri insertUri = context.getContentResolver().insert(
					getPath(getClass()), values);
			id = Long.parseLong(insertUri.getLastPathSegment());
		} else {
			context.getContentResolver().update(getPath(getClass(), id),
					values, null, null);
			// TODO test return value and log error or something
		}
	}

	public boolean delete(Context context) {
		boolean result = context.getContentResolver().delete(
				getPath(getClass(), id), null, null) > 0;
		if (result) {
			id = -1;
		}
		return result;
	}

	private Map<String, Object> asMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		Field[] fields = SQLHelper.getFields(getClass());
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				res.put(field.getName(), field.get(this));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	private void loadCursor(Cursor content) {
		Field[] fields = SQLHelper.getFields(getClass());
		for (Field field : fields) {
			TypeHandler handler = TypeHandler.getHandler(field.getType());
			try {
				field.set(this, handler.fromCursor(field.getName(), content));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static <T extends Storable> T getById(Context context,
			Class<T> clazz, long id) {
		try {
			T result = clazz.newInstance();
			Cursor content = context.getContentResolver().query(
					getPath(clazz, id), null, null, null, null);
			if (content.moveToFirst()) {
				result.loadCursor(content);
			}
			return result;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
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

	public static Cursor list(Context context, Class<? extends Storable> clazz) {
		return context.getContentResolver().query(getPath(clazz), null, null,
				null, null);
	}
}
