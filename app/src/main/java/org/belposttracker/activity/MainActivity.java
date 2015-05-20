package org.belposttracker.activity;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import org.belposttracker.R;
import org.belposttracker.db.model.Parcel;
import org.belposttracker.general.Constant;
import org.belposttracker.general.Utils;
import org.belposttracker.service.DbService;
import org.belposttracker.service.ParcelService;
import org.belposttracker.service.Services;
import org.belposttracker.service.StatusService;
import org.belposttracker.ui.TrackListAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public static final String KEY_PARCEL_ID	= "keyParcelId";
	
	public static final String DELIVERED		= "Доставлено, вручено";
	
	public static final String NO_HISTORY		= "Нет данных";
	
	public static final String WAIT				= "Ожидайте..."; 
	
	private EditText etParcelNumber;
	
	private EditText etParcelName;
	
	private ListView lvTrackList;
	
	private TextView textView;
	
	private LinkedList<String> dateTime	= new LinkedList<String>(); 
	
	private LinkedList<String> history	= new LinkedList<String>(); 
		
	
	public void onSearchClick(View v) throws InterruptedException, ExecutionException {
		String myTrack = etParcelNumber.getText().toString().toUpperCase();
		String myName = etParcelName.getText().toString().toUpperCase();
		
		if (!myName.isEmpty()) {
			if (myTrack.isEmpty()) {   
				Toast.makeText(MainActivity.this, R.string.enter_parcel_number, Toast.LENGTH_SHORT).show();
			} else {
				//checking whether there is this track in the database
				ParcelService ps = ParcelService.getInstance();
				Cursor cursor = ps.hasTrackNumber(myTrack);
				
				if (!cursor.moveToNext()) {
					//database does not have this track, save track in db
					new GetParcelDataTask(myTrack, myName).execute(this);
					
				} else {
					//database has this track
					Parcel my_parcel = ParcelService.getInstance().cursor2Entity(cursor);
					updateParcelHistory(my_parcel);
				}
			} 
		} else {
			Toast.makeText(MainActivity.this, R.string.enter_parcel_name, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	public void updateParcelHistory(Parcel parcel) {
		long parcel_id = parcel.id;
		
		if (parcel.updatable == 0) {
			//get old data from db
			startParselHistoryActivity(parcel_id);
		} else {
			new GetParcelDataTask2(parcel.number, parcel_id, parcel.name).execute(this);
		}
	}
	
	public void onUpdateAllClick(View v) {
		new UpdateAllTask().execute(this);
	}

	public void onBackPressed() {
		moveTaskToBack(true);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		LinkedList<Parcel> trackList = ParcelService.getInstance().getTrackList();
		
		if (trackList.isEmpty()) {
			textView.setVisibility(View.GONE);
		} else {
			textView.setVisibility(View.VISIBLE);
		}
		
		TrackListAdapter trackListAdapter = new TrackListAdapter(this, trackList);
		lvTrackList.setAdapter(trackListAdapter);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Services.getInstance().init(getApplicationContext());
		DbService.getInstance().getReadableDb().close();
		
		textView = (TextView) findViewById(R.id.textView1);
		lvTrackList	= (ListView) findViewById(R.id.lvTrackList);
		etParcelNumber	= (EditText) findViewById(R.id.etParcelNumber);
		etParcelName	= (EditText) findViewById(R.id.etParcelName);
	
		//only numbers and letters are allowed for input
		etParcelNumber.setFilters(new InputFilter[] {
				new InputFilter() {
					@Override
					public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
						if(source.equals("")){
							return source;
						}
						if(source.toString().matches("[0-9a-zA-Z]+")){
							return source;
						}
						return "";
					}
				}
			});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	//get track information and put her in map
	private boolean connectToBelPost(String trackNumber) {
		
		Document doc;
		String url = "http://search.belpost.by/ajax/search?item=" + trackNumber + "&internal=2";
		
		try {
			doc = Jsoup.connect(url).get();
			Elements tableList = doc.getElementsByTag("tbody");

			for (Iterator<Element> tableIterator = tableList.iterator(); tableIterator.hasNext();) {
				Element table = tableIterator.next();
				Elements recordList = table.getElementsByTag("tr");
				
				for (Iterator<Element> recordIterator = recordList.iterator(); recordIterator.hasNext();) {
					Element record = recordIterator.next();
				
					if (record.getElementsByTag("td").first().hasAttr("class")) {
						recordIterator.remove();
					}	
				}
				
				for (Iterator<Element> recordIterator = recordList.iterator(); recordIterator.hasNext();) {
					Element record = recordIterator.next();
					
					Elements dataList = record.getElementsByTag("td");
					String str1 = dataList.remove(0).text();
					String str2 = dataList.text();
					
					dateTime.add(str1);
					history.add(str2);
				}
			}
			
			Collections.reverse(dateTime);
			Collections.reverse(history);
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;	
	}
	
	
	private void startParselHistoryActivity(long parcelId) {
		Intent intent = new Intent(MainActivity.this, ParcelHistoryActivity.class);
		intent.putExtra(KEY_PARCEL_ID, parcelId);
		startActivity(intent);
	}

	private void saveParcelHistory(long parcelId){
		org.belposttracker.db.model.Status status;
		StatusService ss = StatusService.getInstance();
		
		Iterator<String> it1 = dateTime.iterator();
		
		for (Iterator<String> it2 = history.iterator(); (it1.hasNext() && it2.hasNext());)
		{
			status = new org.belposttracker.db.model.Status(parcelId, it1.next(), it2.next());
			ss.save(status);
		}
	}
	
	
	private class GetParcelDataTask extends AsyncTask<Object, Object, Boolean> {
		String trackNumber;
		String parcelName;
		ProgressDialog dialog;
		
		GetParcelDataTask(String trackNumber, String parcelName) {
			this.trackNumber = trackNumber;
			this.parcelName = parcelName;
		}
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();  
	        
			dateTime.clear();
			history.clear();

            dialog = ProgressDialog.show(MainActivity.this, null, WAIT, false, true);
	    }

		
		@Override
		protected Boolean doInBackground(Object...obj) {	
			return connectToBelPost(trackNumber);
		}	
		
		@Override
		protected void onPostExecute(Boolean connect) {
			dialog.dismiss();	
			
			long parcel_id;
			
			if (!history.isEmpty()) {
				String lastStatus = history.peek();
				long lastUpdateDate = Utils.stringToMillis(dateTime.peek());
				int updatable = (lastStatus.contains(DELIVERED) == true) ? 0 : 1;
				
				Parcel parcel = new Parcel(trackNumber, parcelName, updatable, lastUpdateDate);
				ParcelService.getInstance().save(parcel);
				
				parcel_id = ParcelService.getInstance().getByTrack(trackNumber).id;
				saveParcelHistory(parcel_id);
			} else {
				    Parcel parcel = new Parcel(trackNumber, parcelName, 1, System.currentTimeMillis());
				    ParcelService.getInstance().save(parcel);
				    
				    parcel_id = ParcelService.getInstance().getByTrack(trackNumber).id;
				    String dateTime = Constant.format1.format(new Date(System.currentTimeMillis()));
				    
				    org.belposttracker.db.model.Status status = new org.belposttracker.db.model.Status(parcel_id, dateTime, NO_HISTORY);
				    StatusService.getInstance().save(status);
			}
			
			startParselHistoryActivity(parcel_id);
		}
	}
	
	
	private class GetParcelDataTask2 extends AsyncTask<Object, Object, Boolean> {
		String trackNumber;
		String parcelName;
		long parcel_id;
		ProgressDialog dialog;
		
		GetParcelDataTask2(String trackNumber, long parcel_id, String parcelName) {
			this.trackNumber = trackNumber;
			this.parcel_id =  parcel_id;
			this.parcelName = parcelName;
		}
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();  
	        
			dateTime.clear();
			history.clear();

            dialog = ProgressDialog.show(MainActivity.this, null, WAIT, false, true);
	    }

		
		@Override
		protected Boolean doInBackground(Object...obj) {	
			return connectToBelPost(trackNumber);
		}	
		
		@Override
		protected void onPostExecute(Boolean connect) {
			dialog.dismiss();
				
			if ((connect == true) && (!history.isEmpty())) {
					
				String lastStatus = history.peek();
				long lastUpdateDate = Utils.stringToMillis(dateTime.peek());
				int updatable = (lastStatus.contains(DELIVERED) == true) ? 0 : 1;
				
				Parcel parcel = new Parcel(parcel_id, trackNumber, parcelName, updatable, lastUpdateDate);
				ParcelService.getInstance().save(parcel);
				
				//delete old history from table "Status" and add new history
				StatusService.getInstance().deleteByParcelId(parcel_id);
				saveParcelHistory(parcel_id);
			}
			
			startParselHistoryActivity(parcel_id);			
		}
	}
	
	private class UpdateAllTask extends AsyncTask<Object, Object, Object> {
		ProgressDialog dialog;
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();  
	        
            dialog = ProgressDialog.show(MainActivity.this, null, WAIT, false, true);
	    }

		
		@Override
		protected Void doInBackground(Object...obj) {
			
			LinkedList<Parcel> parcelList = ParcelService.getInstance().getTrackList();
			
			for (Parcel parcel: parcelList) {
				if (parcel.updatable != 0) {
					long parcel_id = parcel.id;
					
					dateTime.clear();
					history.clear();
					 
					boolean connect = connectToBelPost(parcel.number);
					
					if ((connect == true) && (!history.isEmpty())) {
						
						String lastStatus = history.peek();
						long lastUpdateDate = Utils.stringToMillis(dateTime.peek());
						int updatable = (lastStatus.contains(DELIVERED) == true) ? 0 : 1;
						
						Parcel new_parcel = new Parcel(parcel_id, parcel.number, parcel.name, updatable, lastUpdateDate);
						ParcelService.getInstance().save(new_parcel);

						
						//delete old history from table "Status" and add new history
						StatusService.getInstance().deleteByParcelId(parcel_id);
						saveParcelHistory(parcel_id);						
					}
				}
			}

			return null;
		}	
		
		@Override
		protected void onPostExecute(Object obj) {
			dialog.dismiss();
			onResume();
		}
	}

}
