package com.njzk2.simplestorage.handler;

import android.database.Cursor;

public class IntHandler extends TypeHandler {

	@Override
	public String getSQLType() {
		return "INTEGER";
	}
	@Override
	public Integer fromCursor(String name, Cursor content) {
		return content.getInt(content.getColumnIndex(name));
	}
}
