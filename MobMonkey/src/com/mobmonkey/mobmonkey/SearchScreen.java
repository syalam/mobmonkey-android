							package com.mobmonkey.mobmonkey;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.mobmonkey.mobmonkey.utils.ExpandedListView;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMArrayAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMHashMap;
import com.mobmonkey.mobmonkeyapi.adapters.MMSearchLocationAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchScreen extends Activity implements LocationListener {
	private static final String TAG = "SearchScreen: ";
	SharedPreferences userPrefs;
	
	LocationManager locationManager;
	Location location;
	double longitudeValue;
	double latitudeValue;
	
	ProgressDialog progressDialog;
	EditText etSearch;
	
	String searchCategory;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		setContentView(R.layout.search_screen);
		
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			promptEnableGPS();
	    }
		
		getCurrentLocation();
		
		init();
		Log.d(TAG, TAG + "LOCATION: Longitude: " + longitudeValue + " Latitude: " + latitudeValue);
	}
	
	/**
	 * Does not close the current {@link Activity} when back button is pressed
	 */
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
		return;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 0) {
			if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				getCurrentLocation();
			} else {
				Toast.makeText(SearchScreen.this, R.string.toast_not_enable_gps, Toast.LENGTH_SHORT).show();
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onLocationChanged(Location location) {
		
	}

	public void onProviderDisabled(String provider) {
		
	}

	public void onProviderEnabled(String provider) {
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
	
	/**
	 * Prompt the user to enable GPS on their device
	 */
	private void promptEnableGPS() {
	    new AlertDialog.Builder(SearchScreen.this)
	    	.setTitle(R.string.title_enable_gps)
	    	.setMessage(R.string.message_enable_gps)
	    	.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            // Launch settings, allowing user to make a change
		            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		        }
	    	})
	    	.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            // No location service, no Activity
		        	Toast.makeText(SearchScreen.this, R.string.toast_not_enable_gps, Toast.LENGTH_SHORT).show();
		            finish();
		            // TODO: not close activity but return back to previous tab
		        }
	    	})
	    	.show();
	}
	
	/**
	 * Function that get the latitude and longitude coordinates of the current location
	 */
	private void getCurrentLocation() {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, SearchScreen.this);
		location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location != null) {
			longitudeValue = location.getLongitude();
			latitudeValue = location.getLatitude();
			DecimalFormat twoDForm = new DecimalFormat("#.######");
			latitudeValue = Double.valueOf(twoDForm.format(latitudeValue));
			longitudeValue = Double.valueOf(twoDForm.format(longitudeValue));
		}
	}
	
	/**
	 * Initialize all the variables and set the appropriate listeners
	 */
	private void init() {
		etSearch = (EditText) findViewById(R.id.etsearch);
		ExpandedListView elvSearchNoCategory = (ExpandedListView) findViewById(R.id.elvsearchnocategory);
		ExpandedListView elvSearchCategory = (ExpandedListView) findViewById(R.id.elvsearchcategory);
		
		etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				Log.d(TAG, TAG + "actionId: " + actionId);
				if(actionId == EditorInfo.IME_ACTION_SEARCH) {
					searchCategory = etSearch.getText().toString();
					
					HashMap<String, Object> hashMap = MMHashMap.getInstance(MMConstants.PARTNER_ID);
					hashMap.put(MMAPIConstants.KEY_USER, userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING));
					hashMap.put(MMAPIConstants.KEY_AUTH, userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
					hashMap.put(MMAPIConstants.KEY_LATITUDE, Double.toString(latitudeValue));
					hashMap.put(MMAPIConstants.KEY_LONGITUDE, Double.toString(longitudeValue));
					hashMap.put(MMAPIConstants.KEY_NAME, searchCategory);
					
					MMSearchLocationAdapter.searchTextWithLocation(new SearchCallback(), hashMap);
					progressDialog = ProgressDialog.show(SearchScreen.this, MMAPIConstants.DEFAULT_STRING, "searching for " + searchCategory + "...", true, false);
				}
				return true;
			}
		});
		
		int[] categoryIcons = new int[]{R.drawable.cat_icon_show_all_nearby, 
										R.drawable.cat_icon_history};
		ArrayAdapter<Object> arrayAdapter = new MMArrayAdapter(SearchScreen.this, R.layout.expanded_listview_row, categoryIcons, getResources().getStringArray(R.array.search_nocategory), android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD);
		elvSearchNoCategory.setAdapter(arrayAdapter);
		elvSearchNoCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				if(position == 0) {				
					searchCategory = ((TextView) view.findViewById(R.id.tvcategory)).getText().toString();
					
					HashMap<String, Object> hashMap = MMHashMap.getInstance(MMConstants.PARTNER_ID);
					hashMap.put(MMAPIConstants.KEY_USER, userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING));
					hashMap.put(MMAPIConstants.KEY_AUTH, userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
					hashMap.put(MMAPIConstants.KEY_LATITUDE, Double.toString(latitudeValue));
					hashMap.put(MMAPIConstants.KEY_LONGITUDE, Double.toString(longitudeValue));
					
					MMSearchLocationAdapter.searchAllNearby(new SearchCallback(), hashMap);
					progressDialog = ProgressDialog.show(SearchScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_search_all_nearby), true, false);
				} else {
					// TODO:
				}
			}
		});
		
		categoryIcons = new int[]{R.drawable.cat_icon_automotive, 
								  R.drawable.cat_icon_travel, 
								  R.drawable.cat_icon_sports, 
								  R.drawable.cat_icon_healthcare, 
								  R.drawable.cat_icon_landmarks, 
								  R.drawable.cat_icon_social, 
								  R.drawable.cat_icon_community_government, 
								  R.drawable.cat_icon_retail, 
								  R.drawable.cat_icon_services_supplies, 
								  R.drawable.cat_icon_transportation};
		arrayAdapter = new MMArrayAdapter(SearchScreen.this, R.layout.expanded_listview_row, categoryIcons, getResources().getStringArray(R.array.search_category), android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD);
		elvSearchCategory.setAdapter(arrayAdapter);
		elvSearchCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				// TODO: 
			}
		});
	}
	
	/**
	 * @author Dezapp, LLC
	 * 
	 */
	private class SearchCallback implements MMCallback {
		public void processCallback(Object obj) {
			if(progressDialog != null) {
				progressDialog.dismiss();
			}
			
			if(obj == null) {
				Log.d(TAG, TAG + "The response object is empty");
			} else {
				Intent searchResultsIntent = new Intent(SearchScreen.this, SearchResultsScreen.class);
				searchResultsIntent.putExtra(MMAPIConstants.INTENT_EXTRA_SEARCH_RESULT_TITLE, searchCategory);
				searchResultsIntent.putExtra(MMAPIConstants.INTENT_EXTRA_SEARCH_RESULTS, (String) obj);
				startActivity(searchResultsIntent);
			}
			
			Log.d(TAG, TAG + "response: " + (String) obj);
		}
	}
}
