package org.belposttracker.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.belposttracker.R;
import org.belposttracker.db.model.Status;
import org.belposttracker.service.ParcelService;
import org.belposttracker.service.StatusService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;


public class ParcelHistoryActivity extends Activity {
	
	public static final String KEY_DATE_TIME = "keyDataTime";
	
	public static final String KEY_HISTORY = "keyHistory";
	
	private long parcel_id;
	
	private List <Map <String, Object>> listForAdapter = new LinkedList <Map <String, Object>>(); 
	
	private Button butSubscribe;
	
	private SimpleAdapter sAdapter;
	
	private ListView lvParcelDataList;
	
	
	public void onSubscribeClick(View v) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.alert_title);
		alert.setMessage(R.string.alert_message);
		
		alert.setPositiveButton(R.string.buy, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//NOP
			}
		});

		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//NOP
			}
		});

		alert.show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parcel_history);
		
		butSubscribe = (Button) findViewById(R.id.butSubscribe);
		lvParcelDataList = (ListView) findViewById(R.id.lvParcelDataList);
		
		parcel_id = getIntent().getLongExtra(MainActivity.KEY_PARCEL_ID, 0);
		this.setTitle(ParcelService.getInstance().findById(parcel_id).number);
	}
	
	protected void onResume() {
		super.onResume();
		
		fillMapListForAdapter();
		createAdapter();
		
		lvParcelDataList.setAdapter(sAdapter);
	}
	
	private void fillMapListForAdapter() {
		listForAdapter.clear();
		StatusService ss = StatusService.getInstance();
		Cursor cursor = ss.getParcelHistory(parcel_id);
		
		Map <String, Object> m;
		
		while (cursor.moveToNext()) {
			Status status = ss.cursor2Entity(cursor);
			m = new HashMap <String, Object>();
			
			m.put(KEY_DATE_TIME, status._date);
			m.put(KEY_HISTORY, status.description);
			
			listForAdapter.add(m);
		}
	}
	
	private void createAdapter() {
		final String[] from = {KEY_DATE_TIME, KEY_HISTORY};
		final int[] to = {R.id.tvDateTime, R.id.tvStatus};
		sAdapter = new SimpleAdapter(this, listForAdapter, R.layout.activity_parcel_history_item, from, to);
	}
}
