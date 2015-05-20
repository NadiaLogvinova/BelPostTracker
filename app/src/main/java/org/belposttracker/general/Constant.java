package org.belposttracker.general;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;


public interface Constant {

	Charset CP_1251 = Charset.forName("CP1251");

	String dateFormat_1 = "dd.MM.yyyy HH:mm:ss";
	
	String dateFormat_2 = "yyyy-MM-dd HH:mm:ss";
	
    SimpleDateFormat format1 = new SimpleDateFormat(Constant.dateFormat_1);
    
    SimpleDateFormat format2 = new SimpleDateFormat(Constant.dateFormat_2);


}
