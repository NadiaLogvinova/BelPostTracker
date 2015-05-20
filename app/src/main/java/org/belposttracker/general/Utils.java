package org.belposttracker.general;

import java.io.Closeable;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Pattern;

import android.database.Cursor;


public abstract class Utils {

	private static final Pattern blank = Pattern.compile("^\\s*$");
	
	private Utils() { }

	public static void closeQuietly(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException e) {
			// ignore
		}
	}

	public static boolean isBlank(String string) {
		if (isEmpty(string)) {
			return true;
		}

		return blank.matcher(string).matches();
	}

	public static boolean isEmpty(String string) {
		return (null == string || string.isEmpty());
	}

	public static long stringToMillis (String dateTimeString) {
        Date date;
		try {
			date = Constant.format1.parse(dateTimeString);
		} catch (ParseException e) {
			try {
				date = Constant.format2.parse(dateTimeString);
			} catch (ParseException e1) {
				date = new Date(0);
			}
		}

       return date.getTime();
    }

}
