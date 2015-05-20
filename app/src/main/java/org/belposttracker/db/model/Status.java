package org.belposttracker.db.model;

import android.content.ContentValues;


public class Status extends Entity {

	public static final String TABLE = "status";

	public static final String COL_PARCEL_ID = "parcel_id";
	
	public static final String COL_DATE = "_date";
	
	public static final String COL_DESCRIPTION = "description";
	
	public static final String[] COLUMNS = new String[]{COL_ID, COL_PARCEL_ID, COL_DATE, COL_DESCRIPTION};

	
	public long parcel_id;
	
	public String _date;

	public String description;

	public Status() {
	}

	public Status(long id, long parcel_id, String _date, String description) {
		this.id				= id;
		this.parcel_id		= parcel_id;
		this._date			= _date;
		this.description	= description;
	}
	
	public Status(long parcel_id, String _date, String description) {
		this.parcel_id		= parcel_id;
		this._date			= _date;
		this.description	= description;
	}

	@Override
	public ContentValues values() {
		// TODO Auto-generated method stub
		ContentValues result = new ContentValues(COLUMNS.length);

		result.put(COL_PARCEL_ID, this.parcel_id);
		result.put(COL_DATE, this._date);
		result.put(COL_DESCRIPTION, this.description);

		return result;
	}
}
