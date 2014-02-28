package com.njzk2.simplestorage.handler;

import android.database.Cursor;

public class LongHandler extends TypeHandler {

	@Override
	public String getSQLType() {
		return "INTEGER";
	}
	@Override
	public Long fromCursor(String name, Cursor content) {
		return content.getLong(content.getColumnIndex(name));
	}
}
