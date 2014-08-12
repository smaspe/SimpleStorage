package com.njzk2.simplestorage.handler;

import android.database.Cursor;

public class BytesHandler extends TypeHandler<byte[]> {

	@Override
	public String getSQLType() {
		return "BLOB";
	}
	@Override
	public byte[] fromCursor(String name, Cursor content) {
		return content.getBlob(content.getColumnIndex(name));
	}

}
