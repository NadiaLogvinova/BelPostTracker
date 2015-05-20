package org.belposttracker.ui;

import java.util.LinkedList;

import org.belposttracker.R;
import org.belposttracker.activity.MainActivity;
import org.belposttracker.db.model.Parcel;
import org.belposttracker.db.model.Status;
import org.belposttracker.service.ParcelService;
import org.belposttracker.service.StatusService;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


public class TrackListAdapter extends BaseAdapter {
	
	Context ctx;
	LayoutInflater lInflater;
	LinkedList<Parcel> objects;
	 

	public TrackListAdapter(Context context, LinkedList<Parcel> parcels) {
		ctx = context;
		objects = parcels;
		lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public Object getItem(int position) {
		return objects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public Parcel getProduct(int position) {
		return ((Parcel) getItem(position));
	}	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
	
		if (view == null) {
			view = lInflater.inflate(R.layout.activity_main_item, parent, false);
		}

		final Parcel parcel = getProduct(position);

		//fill one list position: two buttons and one textView
		setTextForTextView(view, parcel);
		
		Button butDel = (Button) view.findViewById(R.id.butDel);
		
		butDel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ParcelService.getInstance().deleteById(parcel.id);
				StatusService.getInstance().deleteByParcelId(parcel.id);
				
				((MainActivity) ctx).onResume();
			}
		});
	
		butDel.setTag(position);
	
		Button butTrack = (Button) view.findViewById(R.id.butTrack);
		butTrack.setText(parcel.name);
		
		butTrack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((MainActivity) ctx).updateParcelHistory(parcel);
			}
		});
		
		butTrack.setTag(position);
 
		return view;
	}
	
	private void setTextForTextView(View view, Parcel parcel) {
		if (parcel.updatable == 0) {
			((TextView) view.findViewById(R.id.tvStatus)).setText(R.string.delivered);
		} else {
			StatusService ss = StatusService.getInstance();
			Cursor cursor = ss.getParcelHistory(parcel.id);
			
			cursor.moveToFirst();
			Status status =	ss.cursor2Entity(cursor);
			 
			if (status.description.equals(MainActivity.NO_HISTORY)) {
				((TextView) view.findViewById(R.id.tvStatus)).setText(R.string.unknown);
			} else {
				((TextView) view.findViewById(R.id.tvStatus)).setText(R.string.in_transit);
			}	
		}
	}

}
