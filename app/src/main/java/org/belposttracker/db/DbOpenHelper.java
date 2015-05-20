package org.belposttracker.db;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.belposttracker.general.Constant;
import org.belposttracker.general.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DbOpenHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "belposttracker.db";

	private static final String PATCH_LOCATION = "/org/belposttracker/db/data/";

	private static final int DB_VERSION = 1;

	public DbOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (int i = 1; i <= DB_VERSION; i++) {
			executeDbPatch(db, i);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (int i = oldVersion + 1; i <= newVersion; i++) {
			executeDbPatch(db, i);
		}
	}

	private void executeDbPatch(SQLiteDatabase db, int patchVersion) {
		try {
			InputStream is = openPatch(patchVersion);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(is, Constant.CP_1251));
				try {
					String statement = br.readLine();
					do {
						if (!Utils.isBlank(statement) && !statement.startsWith("#")) {
							db.execSQL(statement);
						}
						statement = br.readLine();
					} while (statement != null);
				} catch (IOException e) {
					Log.e("apk", "Could not read patch file.", e);
					throw new RuntimeException("Could not read patch file.", e);
				} finally {
					Utils.closeQuietly(br);
				}
			} finally {
				Utils.closeQuietly(is);
			}
		} catch (FileNotFoundException e1) {
			Log.e("apk", "Could not find patch file.", e1);
			throw new RuntimeException("Could not find patch file.", e1);
		}
	}

	private InputStream openPatch(int patchVersion) throws FileNotFoundException {
		InputStream result = getClass().getResourceAsStream(PATCH_LOCATION + patchVersion + ".sql");

		if (null == result) {
			throw new FileNotFoundException("Could not load patch file:" + patchVersion + ".sql");
		} else {
			return result;
		}
	}
	
}
