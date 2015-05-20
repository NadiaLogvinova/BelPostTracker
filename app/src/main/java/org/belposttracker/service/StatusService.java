
package org.belposttracker.service;

import org.belposttracker.db.model.Status;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class StatusService extends EntityService<Status> {

	private static StatusService instance = Services.getInstance().buildStatusService();


	public static StatusService getInstance() {
		return instance;
	}

	@Override
	public Status cursor2Entity(Cursor cursor) {
		Status result = new Status(cursor.getLong(cursor.getColumnIndex(Status.COL_ID)),
				cursor.getLong(cursor.getColumnIndex(Status.COL_PARCEL_ID)),
				cursor.getString(cursor.getColumnIndex(Status.COL_DATE)),
				cursor.getString(cursor.getColumnIndex(Status.COL_DESCRIPTION)));
		return result;
	}
	
	public Cursor getParcelHistory(long parcelId) {
		SQLiteDatabase db = DbService.getInstance().getReadableDb();
		StatusService ss = StatusService.getInstance();
		
		Cursor cursor = db.query(ss.getTableName(), null, Status.COL_PARCEL_ID + "=?", new String[] {String.valueOf(parcelId)}, null, null, null, null);
		
		return cursor;
	}
	
	public int deleteByParcelId(long parcel_id) {
		SQLiteDatabase db = DbService.getInstance().getReadableDb();
		StatusService ss = StatusService.getInstance();
		
		try {
			return db.delete(ss.getTableName(), Status.COL_PARCEL_ID + "= " + String.valueOf(parcel_id), null);
		} finally {
			db.close();
		}
	}

	@Override
	public String getTableName() {
		return Status.TABLE;
	}

	@Override
	public String[] getColumns() {
		return Status.COLUMNS;
	}

	static void releaseInstance() {
		if (null != instance) {
			instance = null;
		}
	}
}
