package com.njzk2.simplestorage;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
	public static final List<Class<? extends Storable>> TABLES = new ArrayList<Class<? extends Storable>>();

	public Database(Context context) throws NameNotFoundException {
		super(context, "simplestorage", null, context.getPackageManager()
				.getPackageInfo(context.getPackageName(), 0).versionCode);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (Class<? extends Storable> table : TABLES) {
			db.execSQL(new SQLSchema(table).toString());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		for (Class<? extends Storable> table : TABLES) {
			try {
				db.execSQL("drop table " + SQLHelper.getTableName(table));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		onCreate(db);
	}
}
