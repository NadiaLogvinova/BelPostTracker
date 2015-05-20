
package org.belposttracker.service;


import java.util.LinkedList;
import org.belposttracker.db.model.Parcel;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ParcelService extends EntityService<Parcel> {

	private static ParcelService instance = Services.getInstance().buildParcelService();
	

	public static ParcelService getInstance() {
		return instance;
	}
	
	static void releaseInstance() {
		if (null != instance) {
			instance = null;
		}
	}

	@Override
	public Parcel cursor2Entity(Cursor cursor) {
		Parcel result = new Parcel(cursor.getLong(cursor.getColumnIndex(Parcel.COL_ID)),
				cursor.getString(cursor.getColumnIndex(Parcel.COL_NUMBER)),
				cursor.getString(cursor.getColumnIndex(Parcel.COL_NAME)),
				cursor.getInt(cursor.getColumnIndex(Parcel.COL_UPDATABLE)),
				cursor.getLong(cursor.getColumnIndex(Parcel.COL_LAST_UPDATE_DATE)));
		return result;
	}

	@Override
	public String getTableName() {
		return Parcel.TABLE;
	}

	@Override
	public String[] getColumns() {
		return Parcel.COLUMNS;
	}

	public Cursor hasTrackNumber(String trackNumber) {
		//check on existence of such track in the database
		SQLiteDatabase db = DbService.getInstance().getReadableDb();
		ParcelService ps = ParcelService.getInstance();
		
		Cursor cursor = db.query(ps.getTableName(), null, Parcel.COL_NUMBER + "=?", new String[] {trackNumber}, null, null, null, null);
		
		return cursor;
	}
	
	public LinkedList<Parcel> getTrackList() {
		SQLiteDatabase db = DbService.getInstance().getReadableDb();
		LinkedList<Parcel> trackList= new LinkedList<Parcel>();
		
		try {
			Cursor cursor = db.query(Parcel.TABLE, null, null, null, null, null, Parcel.COL_LAST_UPDATE_DATE + " DESC", null);
			try {
				while (cursor.moveToNext()) {
					trackList.add(cursor2Entity(cursor));
				}
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}
		
		return trackList;
	}

	
	public Parcel getByTrack(String track) {
		SQLiteDatabase db = DbService.getInstance().getReadableDb();
		try {
			Cursor cursor = db.query(Parcel.TABLE, null, Parcel.COL_NUMBER + "=?", new String[]{track} , 
					null, null, null, "1");
			try {
				if (cursor.moveToNext()) {
					return cursor2Entity(cursor);
				} else {
					return null;
				}
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}
	}
}
