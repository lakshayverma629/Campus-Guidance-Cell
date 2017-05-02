package com.campusguidance;

import info.androidhive.camerafileupload.R;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;

public class Help extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		// Changing action bar background color
				getActionBar().setBackgroundDrawable(
						new ColorDrawable(Color.parseColor(getResources().getString(
								R.color.action_bar))));
	
	
	}

	

}
