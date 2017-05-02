package com.campusguidance;

import info.androidhive.camerafileupload.R;
import info.camerafileupload.JSONParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Change_pw extends Activity {
	String username;
	TextView paswrd;
	TextView repaswrd;

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// single product url
	private static final String url_product_detials = "http://14.102.36.122/cgc/change_pass.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		return true;
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

			showAlertDialog(Change_pw.this, "No Internet Connection",
					"You don't have internet connection.", false);
		}

	}

	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Setting Dialog Title
		alertDialog.setTitle(title);
		alertDialog.setCancelable(false);
		// Setting Dialog Message
		alertDialog.setMessage(message);

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
		setContentView(R.layout.activity_change_pw);

		// Changing action bar background color
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor(getResources().getString(
						R.color.action_bar))));

		paswrd = (TextView) findViewById(R.id.inputpwd);
		repaswrd = (TextView) findViewById(R.id.inputrepwd);
		Button change = (Button) findViewById(R.id.btnChangepw);

		Intent in = getIntent();
		username = in.getStringExtra("username");
		change.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String pass = paswrd.getText().toString();
				String repass = repaswrd.getText().toString();
				if (pass.equals(repass)) {

					Boolean isInternetPresent = false;
					ConnectionDetector cd;
					cd = new ConnectionDetector(getApplicationContext());
					isInternetPresent = cd.isConnectingToInternet();

					// check for Internet status
					if (isInternetPresent) {
						new SaveProductDetails().execute();

					} else {
						// Internet connection is not present
						// Ask user to connect to Internet

						showAlertDialog(Change_pw.this,
								"No Internet Connection",
								"You don't have internet connection.", false);
					}

				} else
					Toast.makeText(getApplicationContext(),
							"Passwords do not match..", 5000).show();

			}
		});

	}

	/**
	 * Background Async Task to Save product Details
	 * */
	class SaveProductDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Change_pw.this);
			pDialog.setMessage("Changing Password..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Saving product
		 * */
		protected String doInBackground(String... args) {

			// getting updated data from EditTexts
			String pass = paswrd.getText().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("password", pass));
			params.add(new BasicNameValuePair("username", username));

			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_product_detials,
					"POST", params);

			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {

					finish();
					// Toast.makeText(getApplicationContext(),
					// "Password has been changed...", 3000).show();

					// successfully updated
				} else {
					// failed to update product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product uupdated
			pDialog.dismiss();
		}
	}

}
