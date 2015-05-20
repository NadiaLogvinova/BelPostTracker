package org.belposttracker.db.model;

import android.content.ContentValues;


public class Parcel extends Entity {

	public static final String TABLE = "parcel";

	public static final String COL_NUMBER = "number";
	
	public static final String COL_NAME = "name";
	
	public static final String COL_UPDATABLE = "updatable";
	
	public static final String COL_LAST_UPDATE_DATE = "last_update_date";
	
	public static final String[] COLUMNS = new String[]{COL_ID, COL_NUMBER, COL_NAME, COL_UPDATABLE, COL_LAST_UPDATE_DATE};


	public String number;
	
	public String name;

	public int updatable;
	
	public long lastUpdateData;

	public Parcel() {
	}

	public Parcel(long id, String number, String name, int updatable, long lastUpdateData) {
		this.id = id;
		this.number = number;
		this.name = name;
		this.updatable = updatable;
		this.lastUpdateData = lastUpdateData;
	}
	
	public Parcel(String number, String name, int updatable, long lastUpdateData) {
		this.number = number;
		this.name = name;
		this.updatable = updatable;
		this.lastUpdateData = lastUpdateData;
	}

	@Override
	public ContentValues values() {
		// TODO Auto-generated method stub
		ContentValues result = new ContentValues(COLUMNS.length);

		result.put(COL_NUMBER, this.number);
		result.put(COL_NAME, this.name);
		result.put(COL_UPDATABLE, this.updatable);
		result.put(COL_LAST_UPDATE_DATE, this.lastUpdateData);

		return result;
	}
}
