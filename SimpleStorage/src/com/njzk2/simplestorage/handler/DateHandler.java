package com.njzk2.simplestorage.handler;

import java.util.Date;

import android.database.Cursor;

public class DateHandler extends TypeHandler<Date> {

	@Override
	public Date fromCursor(String name, Cursor content) {
		int column = content.getColumnIndex(name);
		if (content.isNull(column)) {
			return null;
		}
		return new Date(content.getLong(content.getColumnIndex(name)));
	}

	@Override
	public Long getSQLValue(Date object) {
		if (object == null) {
			return null;
		}
		return object.getTime();
	}
}
