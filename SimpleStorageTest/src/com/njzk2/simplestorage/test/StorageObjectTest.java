package com.njzk2.simplestorage.test;

import android.test.AndroidTestCase;
import android.util.Log;

import com.njzk2.simplestorage.Database;

public class StorageObjectTest extends AndroidTestCase {

	private static final String TAG = StorageObjectTest.class.getSimpleName();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Database.TABLES.clear();
		Database.TABLES.add(StorageObject.class);

	}

	public void testSave() throws Exception {
		StorageObject object = new StorageObject();
		object.name = "Toto";
		object.number = 42;
		assertTrue("Already an id", object.getRowId() == -1);
		object.save(mContext);
		assertFalse("Failed to save", object.getRowId() == -1);
	}

	public void testLoad() throws Exception {
		StorageObject object = new StorageObject();
		object.name = "Toto";
		object.number = 42;
		object.save(mContext);
		object = StorageObject.getById(mContext, StorageObject.class, object.getRowId());
		assertEquals("Name not equal", "Toto", object.name);
		assertEquals("Number not equal", 42, object.number);
		assertFalse("Failed to load", object.getRowId() == -1);
	}
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		getContext().getContentResolver().delete(StorageObject.getPath(StorageObject.class), null, null);
	}
	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		for (String dbName : mContext.databaseList()) {
			Log.d(TAG, "delete " + dbName);
			mContext.deleteDatabase(dbName);
		}
	}
}
