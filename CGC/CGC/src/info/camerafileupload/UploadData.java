package info.camerafileupload;

import info.androidhive.camerafileupload.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.campusguidance.Category;
import com.campusguidance.Change_pw;
import com.campusguidance.ConnectionDetector;

public class UploadData extends Activity {
	String usern;
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	// url to create new product
	private static String url_create_product = "http://14.102.36.122/cgc/add_notice.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";

	// LogCat tag
	private static final String TAG = UploadData.class.getSimpleName();

	// Camera activity request codes
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

	public static final int MEDIA_TYPE_IMAGE = 1;

	private static final int SDCARD_IMAGE_REQUEST_CODE = 200;
	private String sdfilestring;
	private Uri fileUri, filesd; // file url to store image

	private Button btnCapturePicture, btnsenddata, btnsdcardpick;
	EditText inputtype, inputname, inputDesc;
	Spinner type_notice;

	/*
	 * private void loadSavedPreferences() { SharedPreferences sharedPreferences
	 * = PreferenceManager.getDefaultSharedPreferences(this); String name =
	 * sharedPreferences.getString("username", usern); }
	 * 
	 * private void savePreferences(String key, String value) {
	 * SharedPreferences sharedPreferences =
	 * PreferenceManager.getDefaultSharedPreferences(this); Editor editor =
	 * sharedPreferences.edit(); editor.putString(key, value); editor.commit();
	 * 
	 * }
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		return true;
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

			showAlertDialog(UploadData.this, "No Internet Connection",
					"You don't have internet connection.", false);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_data);

		// Changing action bar background color
		// These two lines are not needed
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor(getResources().getString(
						R.color.action_bar))));
		Log.e("chal..", "msg");
		btnsenddata = (Button) findViewById(R.id.btnsenddata);
		btnsdcardpick = (Button) findViewById(R.id.btnSDCarcPick);
		btnCapturePicture = (Button) findViewById(R.id.btnCapturePicture);
		// Edit Text
		inputname = (EditText) findViewById(R.id.inputName);
		// inputtype=(EditText)findViewById(R.id.type);
		inputDesc = (EditText) findViewById(R.id.inputDesc);
		type_notice = (Spinner) findViewById(R.id.type_spn);

		Intent in = getIntent();
		usern = in.getStringExtra("username");
		// savePreferences("username", usern);
		List<String> types = new ArrayList<String>();
		types.add("Higher Studies");
		types.add("Training");
		types.add("General");

		ArrayAdapter<String> dt = new ArrayAdapter<String>(this,
				R.layout.spinner_layout, types);
		dt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		type_notice.setAdapter(dt);
		/**
		 * Capture image button click event
		 */
		btnsenddata.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String name = inputname.getText().toString();
				String description = inputDesc.getText().toString();
				if (name.equals("") || description.equals(""))
					Toast.makeText(getApplicationContext(),
							"Please fill all values..", 5000).show();
				else
					{
					new CreateNewProduct().execute();
										}

			}
		});

		btnsdcardpick.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String name = inputname.getText().toString();
				String description = inputDesc.getText().toString();
				if (name.equals("") || description.equals(""))
					Toast.makeText(getApplicationContext(),
							"Please fill all values..", 5000).show();
				else{
					pickFromSDCard();
					
				}
			}
		});
		btnCapturePicture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// capture picture
				String name = inputname.getText().toString();
				String description = inputDesc.getText().toString();
				if (name.equals("") || description.equals(""))
					Toast.makeText(getApplicationContext(),
							"Please fill all values..", 5000).show();
				else{
					captureImage();
					
				}}
		});

		/**
		 * Record video button click event
		 */

		// Checking camera availability
		if (!isDeviceSupportCamera()) {
			Toast.makeText(getApplicationContext(),
					"Sorry! Your device doesn't support camera",
					Toast.LENGTH_LONG).show();
			// will close the app if the device does't have camera
			finish();
		}
	}

	/**
	 * Checking device has camera hardware or not
	 * */
	private boolean isDeviceSupportCamera() {
		if (getApplicationContext().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/**
	 * Launching camera app to capture image
	 */
	private void captureImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		Log.e("capture image", fileUri.toString());
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		// start the image capture Intent
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
	}

	// --------------sdcard functions
	private void pickFromSDCard() {
		Log.e("pickfromsdcard", "present");
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				SDCARD_IMAGE_REQUEST_CODE);
		Log.e("pickfromsdcard", "end");
	}

	private String getRealPathFromURI(Uri contentURI) {
		String result;
		Cursor cursor = getContentResolver().query(contentURI, null, null,
				null, null);
		if (cursor == null) { // Source is Dropbox or other similar local file
								// path
			result = contentURI.getPath();
		} else {
			cursor.moveToFirst();
			int idx = cursor
					.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			result = cursor.getString(idx);
			cursor.close();
		}
		return result;
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	// --------------end sdcard functions
	/**
	 * Here we store the file url as it will be null after returning from camera
	 * app
	 */
	/*
	 * @Override protected void onSaveInstanceState(Bundle outState) {
	 * super.onSaveInstanceState(outState);
	 * 
	 * // save file url in bundle as it will be null on screen orientation //
	 * changes outState.putParcelable("file_uri", fileUri);
	 * Log.e("save instance", fileUri.toString()); }
	 * 
	 * @Override protected void onRestoreInstanceState(Bundle
	 * savedInstanceState) { super.onRestoreInstanceState(savedInstanceState);
	 * 
	 * // get the file url fileUri =
	 * savedInstanceState.getParcelable("file_uri"); }
	 */
	/**
	 * Receiving activity result method will be called after closing the camera
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if the result is capturing Image
		if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

				// successfully captured the image
				// launching upload activity
				launchUploadActivity(true);

			} else if (resultCode == RESULT_CANCELED) {

				// user cancelled Image capture
				Toast.makeText(getApplicationContext(),
						"User cancelled image capture", Toast.LENGTH_SHORT)
						.show();

			} else {
				// failed to capture image
				Toast.makeText(getApplicationContext(),
						"Sorry! Failed to capture image", Toast.LENGTH_SHORT)
						.show();
			}
		} else if (requestCode == SDCARD_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

				Log.e("after intent", "present");
				// successfully captured the image
				// launching upload activity
				filesd = data.getData();
				Log.e("fileuri", filesd.toString());

				// selectedImagePath = getPath(selectedImageUri);
				// img.setImageURI(selectedImageUri);
				// selectedImageUri = data.getData();
				sdfilestring = getRealPathFromURI(filesd);
				Log.e("end after in", sdfilestring);
				launchUploadActivityforsdcard(true);

			} else if (resultCode == RESULT_CANCELED) {

				// user cancelled Image capture
				Toast.makeText(getApplicationContext(),
						"User cancelled image upload", Toast.LENGTH_SHORT)
						.show();

			} else {
				// failed to capture image
				Toast.makeText(getApplicationContext(),
						"Sorry! Failed to get image", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private void launchUploadActivityforsdcard(boolean isImage) {
		String name = inputname.getText().toString();
		String description = inputDesc.getText().toString();
		String type = type_notice.getSelectedItem().toString();
		Intent i = new Intent(UploadData.this, UploadActivity.class);
		// Log.e("launch", fileUri.getPath().toString());
		i.putExtra("filePath", sdfilestring);
		i.putExtra("isImage", isImage);
		i.putExtra("name", name);
		i.putExtra("description", description);
		i.putExtra("type", type);
		i.putExtra("username", usern);
		startActivity(i);
	}

	private void launchUploadActivity(boolean isImage) {
		String name = inputname.getText().toString();
		String description = inputDesc.getText().toString();
		String type = type_notice.getSelectedItem().toString();
		Intent i = new Intent(UploadData.this, UploadActivity.class);
		Log.e("launch", fileUri.getPath().toString());
		i.putExtra("filePath", fileUri.getPath());
		i.putExtra("isImage", isImage);
		i.putExtra("name", name);
		i.putExtra("description", description);
		i.putExtra("type", type);
		i.putExtra("username", usern);
		startActivity(i);
	}

	/**
	 * ------------ Helper Methods ----------------------
	 * */

	/**
	 * Creating file uri to store image/video
	 */
	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/**
	 * returning image / video
	 */
	private static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				Config.IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(TAG, "Oops! Failed create " + Config.IMAGE_DIRECTORY_NAME
						+ " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.upload, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case R.id.change_password:
			Intent in = new Intent(getApplicationContext(), Change_pw.class);
			in.putExtra("username", usern);
			startActivity(in);
			break;
		case R.id.delete_notice:
			Intent in2 = new Intent(getApplicationContext(),
					Delete_notice.class);
			in2.putExtra("username", usern);
			startActivity(in2);
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);

	}

	/**
	 * Background Async Task to Create new product
	 * */
	class CreateNewProduct extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(UploadData.this);
			pDialog.setMessage("Adding Notice..Please wait..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {

			String name = inputname.getText().toString();
			String description = inputDesc.getText().toString();
			String type = type_notice.getSelectedItem().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", name));
			params.add(new BasicNameValuePair("description", description));
			params.add(new BasicNameValuePair("type", type));
			params.add(new BasicNameValuePair("username", usern));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_create_product,
					"POST", params);

			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully created product
					Intent in = new Intent(getApplicationContext(),
							Category.class);
					startActivity(in);
					// closing this screen
					finish();
				} else {
					// failed to create product
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
			// dismiss the dialog once done
			pDialog.dismiss();
		}

	}
}
