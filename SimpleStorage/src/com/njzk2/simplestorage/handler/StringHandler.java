package com.njzk2.simplestorage.handler;

import android.database.Cursor;

public class StringHandler extends TypeHandler<String> {

	@Override
	public String fromCursor(String name, Cursor content) {;
		return content.getString(content.getColumnIndex(name));
	}
}
