
package org.belposttracker.service;

import org.belposttracker.db.model.Entity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class EntityService <T extends Entity> {

	public T findById(Long id) {
		SQLiteDatabase db = DbService.getInstance().getReadableDb();
		try {
			return findById(id, db);
		} finally {
			db.close();
		}
	}

	public T findById(Long id, SQLiteDatabase db) {
		Cursor cursor = db.query(getTableName(), getColumns(), Entity.COL_ID + "=?", new 
				String[]{id.toString()}, null, null, null, "1");
		try {
			if (cursor.moveToNext()) {
				return cursor2Entity(cursor);
			} else {
				return null;
			}
		} finally {
			cursor.close();
		}
	}

	public int deleteById(Long id) {
		SQLiteDatabase db = DbService.getInstance().getReadableDb();
		try {
			return db.delete(getTableName(), Entity._ID + "= " + id.toString(), null);
		} finally {
			db.close();
		}
	}
		
	
	public T save(T entity) {
		SQLiteDatabase db = DbService.getInstance().getWritableDb();
		try {
			return save(db, entity);
		} finally {
			db.close();
		}
	}

	public T save(SQLiteDatabase db, T entity) {
		if (!entity.isStored()) {
			return insert(db, entity);
		} else {
			return update(db, entity);
		}
	}

	public T update(SQLiteDatabase db, T entity) {
		db.update(getTableName(), entity.values(), Entity.COL_ID + "=?", new String[]{entity.id.toString()});
		return entity;
	}

	public T insert(SQLiteDatabase db, T entity) {
		entity.id = db.insert(getTableName(), null, entity.values());
		return entity;
	}

	public abstract T cursor2Entity(Cursor cursor);

	public abstract String getTableName();

	public abstract String[] getColumns();

}
