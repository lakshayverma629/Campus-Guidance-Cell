package info.camerafileupload;

import info.androidhive.camerafileupload.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.campusguidance.Category;
import com.campusguidance.ConnectionDetector;
import com.campusguidance.Notice_full;

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

public class Notices extends ListActivity {

	// Progress Dialog
	private ProgressDialog pDialog;
	String imagepath;
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	String type_nt;
	ArrayList<HashMap<String, String>> productsList;

	// url to get all products list
	private static String url_all_products = "http://14.102.36.122/cgc/get_notices.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
	private static final String TAG_NAME = "subject";
	private static final String TAG_DESCRIPTION = "description";
	private static final String TAG_IMAGE = "image_path";
	private static final String TAG_TYPE = "type";

	// products JSONArray
	JSONArray products = null;

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

			showAlertDialog(Notices.this, "No Internet Connection",
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_notices);

		// Changing action bar background color
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor(getResources().getString(
						R.color.action_bar))));

		Intent in = getIntent();
		type_nt = in.getStringExtra("type_notice");
		// Hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();

		// check for Internet status
		Boolean isInternetPresent = false;
		ConnectionDetector cd;
		cd = new ConnectionDetector(getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent) {
			// Loading products in Background Thread
			new LoadAllProducts().execute();

		} else {
			// Internet connection is not present
			// Ask user to connect to Internet

			showAlertDialog(Notices.this, "No Internet Connection",
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
				String img = ((TextView) view.findViewById(R.id.tvimage))
						.getText().toString();
				String type = ((TextView) view.findViewById(R.id.tvtype))
						.getText().toString();

				Intent in = new Intent(getApplicationContext(),
						Notice_full.class);
				in.putExtra(TAG_NAME, name);
				in.putExtra(TAG_DESCRIPTION, desc);
				in.putExtra(TAG_IMAGE, img);
				in.putExtra(TAG_TYPE, type);
				startActivity(in);
				// Starting new intent
				/*
				 * Intent in = new Intent(getApplicationContext(),
				 * EditProductActivity.class); // sending pid to next activity
				 * in.putExtra(TAG_PID, pid);
				 * 
				 * // starting new activity and expecting some response back
				 * startActivityForResult(in, 100);
				 */
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
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllProducts extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(Notices.this);
			pDialog.setMessage("Loading " + type_nt
					+ " notices. Please wait...");
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
			params.add(new BasicNameValuePair("type", type_nt));
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

						String username = c.getString("username");
						imagepath = c.getString("image_name");
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						map.put(TAG_NAME, name);
						map.put(TAG_DESCRIPTION, desc);
						map.put(TAG_IMAGE, imagepath);

						// adding each child node to HashMap key => value
						map.put("username", username);
						// adding HashList to ArrayList
						productsList.add(map);
					}
				} else {
					Log.e("no messages found", "here........");
					showAlertDialog(getApplicationContext(),
							"No messages found", "exit to close", false);

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
				showAlertDialog(Notices.this, "No Internet Connection",
						"You don't have internet connection.", false);

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
						ListAdapter adapter = new SimpleAdapter(Notices.this,
								productsList, R.layout.list_item, new String[] {
										TAG_NAME, TAG_DESCRIPTION, TAG_IMAGE,
										"username", "type_nt" }, new int[] {
										R.id.name, R.id.desc, R.id.tvimage,
										R.id.username, R.id.tvtype });
						// updating listview
						setListAdapter(adapter);
					}

				}
			});

		}

	}
}