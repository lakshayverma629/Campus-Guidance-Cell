package info.camerafileupload;

import info.androidhive.camerafileupload.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.campusguidance.Category;
import com.campusguidance.ConnectionDetector;
import com.campusguidance.Notice_full;

public class Delete_notice extends ListActivity {
	private ProgressDialog pDialog2;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	// Progress Dialog
	private ProgressDialog pDialog;
	String imagepath;
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	String user;
	String id_not;
	ArrayList<HashMap<String, String>> productsList;

	// url to delete notice by giving id
	private static String url_delete_notice = "http://14.102.36.122/cgc/delete_notice.php";

	// url to get all products list
	private static String url_all_products = "http://14.102.36.122/cgc/get_notices_user.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
	private static final String TAG_NAME = "subject";
	private static final String TAG_DESCRIPTION = "description";
	private static final String TAG_USER = "username";
	private static final String TAG_ID = "id";

	// products JSONArray
	JSONArray products = null;

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_notices);

		// Changing action bar background color
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor(getResources().getString(
						R.color.action_bar))));

		Intent in = getIntent();
		user = in.getStringExtra("username");
		// Hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();

		Boolean isInternetPresent = false;
		ConnectionDetector cd;
		cd = new ConnectionDetector(getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();

		// check for Internet status
		if (isInternetPresent) {

			// Loading products in Background Thread
			new LoadAllProducts().execute();

		} else {
			// Internet connection is not present
			// Ask user to connect to Internet

			showAlertDialog(Delete_notice.this, "No Internet Connection",
					"You don't have internet connection.", false);
		}

		// Get listview
		ListView lv = getListView();

		// on seleting single product
		// launching Edit Product Screen
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String name = ((TextView) view.findViewById(R.id.name))
						.getText().toString();
				String desc = ((TextView) view.findViewById(R.id.desc))
						.getText().toString();
				id_not = ((TextView) view.findViewById(R.id.tvimage)).getText()
						.toString();
				Log.e("id", id_not);
				// ----------------------------------------

				// Creating alert Dialog with two Buttons

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						Delete_notice.this);

				// Setting Dialog Title
				alertDialog.setTitle("Confirm Delete...");

				// Setting Dialog Message
				alertDialog.setMessage("Are you sure you want delete this?");

				// Setting Icon to Dialog
				alertDialog.setIcon(R.drawable.delete);

				// Setting Positive "Yes" Button
				alertDialog.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Write your code here to execute after dialog
								Toast.makeText(getApplicationContext(),
										"Notice is deleted..",
										Toast.LENGTH_SHORT).show();
								new DeleteNoticefromserver().execute();

							}
						});
				// Setting Negative "NO" Button
				alertDialog.setNegativeButton("NO",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Write your code here to execute after dialog
								Toast.makeText(getApplicationContext(),
										"You clicked on NO", Toast.LENGTH_SHORT)
										.show();
								dialog.cancel();
							}
						});

				// Showing Alert Message
				alertDialog.show();

				// --------------------------------------------

			}
		});

	}

	// Response from Edit Product Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received
			// means user edited/deleted product
			// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}

	/**
	 * Background Async Task to Save product Details
	 * */
	class DeleteNoticefromserver extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog2 = new ProgressDialog(Delete_notice.this);
			pDialog2.setMessage("Deleting Notice..");
			pDialog2.setIndeterminate(false);
			pDialog2.setCancelable(true);
			pDialog2.show();
		}

		/**
		 * deleting notice
		 * */
		protected String doInBackground(String... args) {

			// getting updated data from EditTexts

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_ID, id_not));

			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_delete_notice,
					"POST", params);

			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {

					finish();
					// Toast.makeText(getApplicationContext(),
					// "Password has been changed...", 3000).show();
					Log.e("sucess", "adadad");
					// successfully updated
				} else {
					Log.e("failed", "adadad");
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
			// dismiss the dialog once product updated
			pDialog.dismiss();
		}
	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllProducts extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(Delete_notice.this);
			pDialog.setMessage("Loading " + user + " notices. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("user", user));
			// getting JSON string from URL

			try {
				JSONObject json = jParser.makeHttpRequest(url_all_products,
						"POST", params);

				// Check your log cat for JSON reponse
				// Log.d("All Products: ", json.toString());

				// Checking for SUCCESS TAG
				int success = 30;
				success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					products = json.getJSONArray(TAG_PRODUCTS);

					// looping through All Products
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// Storing each json item in variable
						String name = c.getString(TAG_NAME);
						String desc = c.getString(TAG_DESCRIPTION);
						String username = c.getString(TAG_USER);
						// imagepath = c.getString("image_name");
						String id = c.getString(TAG_ID);
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						map.put(TAG_NAME, name);
						map.put(TAG_DESCRIPTION, desc);
						map.put(TAG_ID, id);

						// adding each child node to HashMap key => value
						map.put("username", username);
						// adding HashList to ArrayList
						productsList.add(map);
					}
				} else {
					Log.e("no messages found", "here........");

					// Toast.makeText(getApplicationContext(),
					// "No notice found in "+type_nt, 2000).show();
					// no products found
					// Launch Add New product Activity
					/*
					 * Intent i = new Intent(getApplicationContext(),
					 * NewProductActivity.class); // Closing all previous
					 * activities i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					 * startActivity(i);
					 */
				}
			} catch (Exception e) {
				e.printStackTrace();

				// Toast.makeText(getApplicationContext(),
				// "no notice present in "+type_nt, 5000).show();
			}

			return null;
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
					if (!productsList.equals(null)) {
						ListAdapter adapter = new SimpleAdapter(
								Delete_notice.this, productsList,
								R.layout.list_item, new String[] { TAG_NAME,
										TAG_DESCRIPTION, TAG_ID, TAG_USER },
								new int[] { R.id.name, R.id.desc, R.id.tvimage,
										R.id.username });
						// updating listview
						setListAdapter(adapter);
					}

				}
			});

		}

	}
}