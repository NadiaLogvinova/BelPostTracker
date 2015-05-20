package org.belposttracker.service;

import org.belposttracker.db.DbOpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbService {

	private static DbService instance;

	private SQLiteOpenHelper dbOpen;

	private final Context context;

	DbService(Context context) {
		this.context = context;
	}

	public static DbService getInstance() {
		synchronized (DbService.class) {
			if (null == DbService.instance) {
				DbService.instance = Services.getInstance().buildDbService();
			}
		}
		
		return DbService.instance;
	}

	static void releaseInstance() {
		if (null != DbService.instance) {
			DbService.instance.close();
			DbService.instance = null;
		}
	}

	private void close() {
		if (null != dbOpen) {
			dbOpen.close();
		}
	}

	public SQLiteDatabase getWritableDb() {
		return getOpenHelper().getWritableDatabase();
	}

	public SQLiteDatabase getReadableDb() {
		return getOpenHelper().getReadableDatabase();
	}

	private synchronized SQLiteOpenHelper getOpenHelper() {
		if (null == dbOpen) {
			dbOpen = new DbOpenHelper(this.context);
		}
			
		return dbOpen;
	}

}
