package com.njzk2.simplestorage.handler;

import java.util.Arrays;

import org.json.JSONArray;

import android.database.Cursor;

// TODO consider using a generic ArrayHandler that can delegate to simple handlers
public class StringArrayHandler extends TypeHandler<String[]> {

	@Override
	public String[] fromCursor(String name, Cursor content) {
		String val = content.getString(content.getColumnIndex(name));
		if (val == null) {
			return null;
		}
		try {
			JSONArray arr = new JSONArray(val);
			String[] res = new String[arr.length()];
			for (int i = 0; i < res.length; i++) {
				res[i] = arr.getString(i);
			}
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getSQLValue(String[] object) {
		if (object == null) {
			return null;
		}
		return new JSONArray(Arrays.asList(object)).toString();
	}
}
