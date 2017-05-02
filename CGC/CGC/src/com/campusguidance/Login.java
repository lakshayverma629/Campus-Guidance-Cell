package com.campusguidance;

import info.androidhive.camerafileupload.R;
import info.camerafileupload.JSONParser;
import info.camerafileupload.UploadData;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {
	JSONParser jParser = new JSONParser();
	private static String url_all_products = "http://14.102.36.122/cgc/check_login.php";
	private static final String TAG_SUCCESS = "success";
	private ProgressDialog pDialog;

	String usern;
	String pass;

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

			showAlertDialog(Login.this, "No Internet Connection",
					"You don't have internet connection.", false);
		}

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
		setContentView(R.layout.activity_login);
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

		} else {
			// Internet connection is not present
			// Ask user to connect to Internet

			showAlertDialog(Login.this, "No Internet Connection",
					"You don't have internet connection.", false);
		}

		final TextView username = (TextView) findViewById(R.id.etUserName);
		final TextView password = (TextView) findViewById(R.id.etPass);
		Button login = (Button) findViewById(R.id.btnSingIn);

		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				usern = username.getText().toString();
				pass = password.getText().toString();
				if (usern.equals("") || pass.equals(""))
					Toast.makeText(getApplicationContext(),
							"Please fill details..", 5000).show();
				else
					new LoadAllProducts().execute();

			}
		});
	}

	class LoadAllProducts extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Login in...Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {

			// //=------------------------

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("username", usern));
			params.add(new BasicNameValuePair("password", pass));

			try {
				// Checking for SUCCESS TAG
				JSONObject json = jParser.makeHttpRequest(url_all_products,
						"POST", params);

				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					Intent i = new Intent(getApplicationContext(),
							UploadData.class);
					i.putExtra("username", usern);
					// Closing all previous activities
					startActivity(i);
					pDialog.dismiss();
					finish();
					// looping through All Products
				} else {
					Intent in = new Intent(getApplicationContext(), Login.class);
					startActivity(in);
					finish();
					/*
					 * Toast.makeText(getApplicationContext(),
					 * "No username exits..", 3000).show();
					 */
					// no products found
					// Launch Add New product Activity

				}
			} catch (JSONException e) {
				e.printStackTrace();
				// Toast.makeText(getApplicationContext(), "error",
				// 3000).show();
			}

			// --------------------

			return null;
			// Building Parameters

		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
				}
			});

		}

	}

}
