package com.njzk2.simplestorage.handler;

import android.database.Cursor;

public class DoubleHandler extends TypeHandler<Double> {

	@Override
	public String getSQLType() {
		return "REAL";
	}
	@Override
	public Double fromCursor(String name, Cursor content) {
		return content.getDouble(content.getColumnIndex(name));
	}
}
