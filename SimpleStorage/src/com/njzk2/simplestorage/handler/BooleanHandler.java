package com.njzk2.simplestorage.handler;

import android.database.Cursor;

public class BooleanHandler extends TypeHandler<Boolean> {

	@Override
	public Boolean fromCursor(String name, Cursor content) {
		return content.getInt(content.getColumnIndex(name)) != 0;
	}
	@Override
	public String getSQLType() {
		return "INTEGER";
	}
}
