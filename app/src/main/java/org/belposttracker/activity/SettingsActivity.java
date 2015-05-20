package org.belposttracker.activity;

import org.belposttracker.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;



public class SettingsActivity extends Activity {
	
	public static final String URI = "http://www.belpost.by"; 
	
	public void onSiteClick(View v) {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URI)));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}

}