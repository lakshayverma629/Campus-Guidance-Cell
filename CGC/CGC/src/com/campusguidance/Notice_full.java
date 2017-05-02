package com.campusguidance;

import info.androidhive.camerafileupload.R;
import info.camerafileupload.Notices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Notice_full extends Activity {
	ImageView img;
	Bitmap bitmap;
	Button download;
	ProgressDialog pDialog;
	ProgressBar pb;
	Dialog dialog;
	int downloadedSize = 0;
	int totalSize = 0;
	TextView cur_val;
	String imgpat, subject_name, type;

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

			showAlertDialog(Notice_full.this, "No Internet Connection",
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
		setContentView(R.layout.activity_notice_full);

		// Changing action bar background color
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor(getResources().getString(
						R.color.action_bar))));
		final TextView tvname = (TextView) findViewById(R.id.tvname);
		final TextView tvdesc = (TextView) findViewById(R.id.tvdesc);

		Intent in1 = getIntent();
		subject_name = in1.getStringExtra("subject");
		type = in1.getStringExtra("type");

		img = (ImageView) findViewById(R.id.ivimage);
		imgpat = in1.getStringExtra("image_path");

		tvname.setText(subject_name);
		tvdesc.setText(in1.getStringExtra("description"));

		Button share = (Button) findViewById(R.id.btnsharenotice);
		download = (Button) findViewById(R.id.btndownloadimage);

		download.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Boolean isInternetPresent = false;
				ConnectionDetector cd;
				cd = new ConnectionDetector(getApplicationContext());
				isInternetPresent = cd.isConnectingToInternet();

				// check for Internet status
				if (isInternetPresent) {

					showProgress(imgpat);

					new Thread(new Runnable() {
						public void run() {
							downloadFile();
						}
					}).start();

				} else {
					// Internet connection is not present
					// Ask user to connect to Internet

					showAlertDialog(Notice_full.this, "No Internet Connection",
							"You don't have internet connection.", false);
				}

			}
		});
		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(android.content.Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"I want to share this information with you...\n");
				i.putExtra(
						android.content.Intent.EXTRA_TEXT,
						"Subject:\n"
								+ tvname.getText()
								+ "\nClick below link to view image:\n"
								+ imgpat
								+ "\nDescription:\n"
								+ tvdesc.getText()
								+ "\n --This notice is shared via CGC.");
				startActivity(Intent.createChooser(i, "Share notice:"));

			}
		});

		// Toast.makeText(getApplicationContext(), imgpat, 5000).show();
		if (imgpat.equals("null")) {
			img.setVisibility(View.INVISIBLE);
			download.setVisibility(View.INVISIBLE);
			Toast.makeText(getApplicationContext(), "No Image Available..",
					5000).show();
		} else
			new LoadImage().execute(imgpat);

	}

	void downloadFile() {

		try {
			URL url = new URL(imgpat);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);

			// connect
			urlConnection.connect();

			// set the path where we want to save the file
			File SDCardRoot = Environment.getExternalStorageDirectory();
			File destination = new File(SDCardRoot + "/My Saved Notices/");
			destination.mkdirs();
			// create a new file, to save the downloaded file
			File file = new File(destination, subject_name + ".jpg");

			FileOutputStream fileOutput = new FileOutputStream(file);

			// Stream used for reading the data from the internet
			InputStream inputStream = urlConnection.getInputStream();

			// this is the total size of the file which we are downloading
			totalSize = urlConnection.getContentLength();

			runOnUiThread(new Runnable() {
				public void run() {
					pb.setMax(totalSize);
				}
			});

			// create a buffer...
			byte[] buffer = new byte[1024];
			int bufferLength = 0;

			while ((bufferLength = inputStream.read(buffer)) > 0) {
				fileOutput.write(buffer, 0, bufferLength);
				downloadedSize += bufferLength;
				// update the progressbar //
				runOnUiThread(new Runnable() {
					public void run() {
						pb.setProgress(downloadedSize);
						float per = ((float) downloadedSize / totalSize) * 100;
						cur_val.setText("Downloaded " + downloadedSize
								+ "B / " + totalSize + "B (" + (int) per
								+ "%)");
					}
				});
			}
			// close the output stream when complete //
			fileOutput.close();
			runOnUiThread(new Runnable() {
				public void run() {
					// pb.dismiss(); // if you want close it..
				}
			});

		} catch (final MalformedURLException e) {
			showError("Error : MalformedURLException " + e);
			e.printStackTrace();
		} catch (final IOException e) {
			showError("Error : IOException " + e);
			e.printStackTrace();
		} catch (final Exception e) {
			showError("Error : Please check your internet connection " + e);
		}
	}

	void showError(final String err) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(Notice_full.this, err, Toast.LENGTH_LONG).show();
			}
		});
	}

	void showProgress(String file_path) {
		dialog = new Dialog(Notice_full.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.myprogressdialog);
		dialog.setTitle("Download Progress");

		TextView text = (TextView) dialog.findViewById(R.id.tv1);
		text.setText("Downloading image from server... ");
		cur_val = (TextView) dialog.findViewById(R.id.cur_pg_tv);
		cur_val.setText("Starting download...");
		dialog.show();

		pb = (ProgressBar) dialog.findViewById(R.id.progress_bar);
		pb.setProgress(0);
		pb.setProgressDrawable(getResources().getDrawable(
				R.drawable.green_progress));
	}

	private class LoadImage extends AsyncTask<String, String, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// check for Internet status
			Boolean isInternetPresent = false;
			ConnectionDetector cd;
			cd = new ConnectionDetector(getApplicationContext());
			isInternetPresent = cd.isConnectingToInternet();

			if (isInternetPresent) {
				// Loading image in Background Thread
				pDialog = new ProgressDialog(Notice_full.this);
				pDialog.setMessage("Loading Image ....Please wait..");
				pDialog.setCancelable(false);
				pDialog.show();

			} else {
				// Internet connection is not present
				// Ask user to connect to Internet

				showAlertDialog(Notice_full.this, "No Internet Connection",
						"Cannot load image.", false);
			}

		}

		protected Bitmap doInBackground(String... args) {
			try {
				bitmap = BitmapFactory.decodeStream((InputStream) new URL(
						args[0]).getContent());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}

		protected void onPostExecute(Bitmap image) {
			if (image != null) {
				img.setImageBitmap(image);
				pDialog.dismiss();
			} else {
				pDialog.dismiss();
				Toast.makeText(Notice_full.this,
						"Image Does Not exist or Network Error",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
