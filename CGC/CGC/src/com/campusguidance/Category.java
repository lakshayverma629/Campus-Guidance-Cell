package com.campusguidance;

import info.androidhive.camerafileupload.R;
import info.camerafileupload.Notices;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Category extends Activity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);

	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Boolean isInternetPresent = false;
		ConnectionDetector cd;
		cd = new ConnectionDetector(getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();

		// check for Internet status
		if (isInternetPresent) {

		} else {
			// Internet connection is not present
			// Ask user to connect to Internet

			showAlertDialog(Category.this, "No Internet Connection",
					"You don't have internet connection.", false);
		}

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.login_admin:
			Intent in = new Intent(getApplicationContext(), Login.class);
			startActivity(in);
			break;
		case R.id.help:
			Intent in2 = new Intent(getApplicationContext(), Help.class);
			startActivity(in2);
			break;
		case R.id.about_us:
			Intent in3 = new Intent(getApplicationContext(),AboutUs.class);
			startActivity(in3);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);

	}

	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);
		alertDialog.setCancelable(false);
		// Setting alert dialog icon
		alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

		// Setting OK Button
		alertDialog.setButton("EXIT", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				System.exit(0);
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		Button higherstudies = (Button) findViewById(R.id.higherstudies);
		Button training = (Button) findViewById(R.id.training);
		Button general = (Button) findViewById(R.id.general);

		// Changing action bar background color
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor(getResources().getString(
						R.color.action_bar))));

		Boolean isInternetPresent = false;
		ConnectionDetector cd;
		cd = new ConnectionDetector(getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();

		// check for Internet status
		if (isInternetPresent) {
			// Internet Connection is Present
			// make HTTP requests

			/*
			 * showAlertDialog(Category.this, "Internet Connection",
			 * "You have internet connection", true);
			 */

		} else {
			// Internet connection is not present
			// Ask user to connect to Internet

			showAlertDialog(Category.this, "No Internet Connection",
					"You don't have internet connection.", false);
		}

		higherstudies.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent in = new Intent(getApplicationContext(), Notices.class);
				in.putExtra("type_notice", "Higher Studies");
				startActivity(in);
			}
		});

		training.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent in = new Intent(getApplicationContext(), Notices.class);
				in.putExtra("type_notice", "Training");
				startActivity(in);
			}
		});
		general.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent in = new Intent(getApplicationContext(), Notices.class);
				in.putExtra("type_notice", "General");
				startActivity(in);
			}
		});
	}

}
