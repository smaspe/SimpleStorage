package com.njzk2.simplestorage;

import java.util.Stack;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class DataProvider extends ContentProvider {

	// Used for the UriMacher
	private static final int OBJECTS = 1;
	private static final int OBJECT_ID = OBJECTS + 1;

	private static String AUTHORITY = null;

	public static Uri DATA_CONTENT_URI;

	private static UriMatcher sURIMatcher;

	Database mDatabase;

	@Override
	public boolean onCreate() {
		if (AUTHORITY == null) {
			try {
				AUTHORITY = getContext().getPackageName();
				sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
				sURIMatcher.addURI(AUTHORITY, "*", OBJECTS);
				sURIMatcher.addURI(AUTHORITY, "*/#", OBJECT_ID);
				DATA_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
				mDatabase = new Database(getContext());
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public String getType(Uri arg0) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
		long id = 0;
		switch (uriType) {
		case OBJECTS:
			id = sqlDB.insert(uri.getLastPathSegment(), null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.withAppendedPath(uri, Long.toString(id));
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		// Using SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		int uriType = sURIMatcher.match(uri);
		Stack<String> segments = new Stack<String>();
		segments.addAll(uri.getPathSegments());
		switch (uriType) {
		case OBJECT_ID:
			queryBuilder.appendWhere(BaseColumns._ID + "=" + segments.pop());
			// No break
		case OBJECTS:
			// Set the table
			queryBuilder.setTables(segments.pop());
			break;
		}
		Cursor cursor = queryBuilder.query(mDatabase.getReadableDatabase(), projection, selection, selectionArgs, null,
				null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
		int rowsUpdated = 0;
		String table;
		switch (uriType) {
		case OBJECTS:
			table = uri.getLastPathSegment();
			break;
		case OBJECT_ID:
			table = uri.getPathSegments().get(uri.getPathSegments().size() - 2);
			selection = getQuery(uri, selection);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		rowsUpdated = sqlDB.update(table, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
		int rowsDeleted = 0;
		String table;
		switch (uriType) {
		case OBJECTS:
			table = uri.getLastPathSegment();
			break;
		case OBJECT_ID:
			table = uri.getPathSegments().get(uri.getPathSegments().size() - 2);
			selection = getQuery(uri, selection);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		rowsDeleted = sqlDB.delete(table, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	private String getQuery(Uri uri, String selection) {
		String id = uri.getLastPathSegment();
		String query = BaseColumns._ID + "=" + id;
		if (!TextUtils.isEmpty(selection)) {
			query += " and " + selection;
		}
		return query;
	}

}
