
package org.belposttracker.db.model;

import android.content.ContentValues;
import android.provider.BaseColumns;


public abstract class Entity implements BaseColumns {

	public static final String COL_ID = BaseColumns._ID;

	public Long id = null;

	@Override
	public boolean equals(Object o) {
		if (null == o) {
			return false;
		}

		if (!o.getClass().equals(this.getClass())) {
			return false;
		}

		Entity e = (Entity) o;
		if (null == this.id || null == e.id) {
			return false;
		}

		return this.id == e.id;
	}

	public boolean isStored() {
		return null != id;
	}

	public abstract ContentValues values();

}
