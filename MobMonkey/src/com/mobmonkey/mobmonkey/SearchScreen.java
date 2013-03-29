package com.mobmonkey.mobmonkey;

import java.text.DecimalFormat;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkey.utils.MMExpandedListView;
import com.mobmonkey.mobmonkey.utils.MMCategories;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMSearchResultsCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
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
import android.widget.TextView;
import android.widget.Toast;

/**
 * Android {@link Activity} screen displays search locations for the user
 * @author Dezapp, LLC
 *
 */
public class SearchScreen extends Activity implements LocationListener {
	private static final String TAG = "SearchScreen: ";
	
	LocationManager locationManager;
	Location location;
	double longitudeValue;
	double latitudeValue;

	SharedPreferences userPrefs;
	String[] topLevelCategories;
	
	ProgressDialog progressDialog;
	EditText etSearch;
	
	int[] categoryIcons;
	int[] categoryIndicatorIcons;
	
	String searchCategory;
	String selectedCategory;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_screen);
		
		try {
			init();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Handler when back button is pressed, it will not close and destroy the current {@link Activity} but instead it will remain on the current {@link Activity}
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
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			getCurrentLocation();
			searchLocation(requestCode);
		} else {
			Toast.makeText(SearchScreen.this, R.string.toast_not_enable_gps, Toast.LENGTH_SHORT).show();
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	/*
	 * (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location location) {
		
	}

	/*
	 * (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onProviderDisabled(String provider) {
		
	}

	/*
	 * (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onProviderEnabled(String provider) {
		
	}

	/*
	 * (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
	
	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btnfilter:
				startActivity(new Intent(SearchScreen.this, FilterScreen.class));
				break;
			case R.id.btnaddloc:
				checkForGPS(MMAPIConstants.REQUEST_CODE_TURN_ON_GPS_ADD_LOCATION);
				break;
		}
	}
	
	/**
	 * Check if GPS is enabled on user device
	 * @param requestCode
	 */
	private void checkForGPS(int requestCode) {
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			promptEnableGPS(requestCode);
	    } else {
	    	getCurrentLocation();
	    	searchLocation(requestCode);	    	
	    }
	}
	
	/**
	 * Prompt the user to enable GPS on the device
	 */
	private void promptEnableGPS(final int requestCode) {
	    new AlertDialog.Builder(SearchScreen.this)
	    	.setTitle(R.string.ad_title_enable_gps)
	    	.setMessage(R.string.ad_message_enable_gps)
	    	.setCancelable(false)
	    	.setPositiveButton(R.string.ad_btn_yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            // Launch settings, allowing user to make a change
		            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), requestCode);
		        }
	    	})
	    	.setNegativeButton(R.string.ad_btn_no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            // No location service, no Activity
		        	Toast.makeText(SearchScreen.this, R.string.toast_not_enable_gps, Toast.LENGTH_SHORT).show();
//		            finish();
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
			Log.d(TAG, TAG + "lat: " + latitudeValue + " longitude: " + longitudeValue);
		}
	}
	
	/**
	 * Initialize all the variables and set the appropriate listeners
	 * @throws JSONException 
	 */
	private void init() throws JSONException {
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		
		topLevelCategories = MMCategories.getTopLevelCategories(SearchScreen.this.getApplicationContext());
		//topLevelCategories = new JSONArray(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_TOP_LEVEL_CATEGORIES, MMAPIConstants.DEFAULT_STRING));
		
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		
		etSearch = (EditText) findViewById(R.id.etsearch);
		MMExpandedListView elvSearchNoCategory = (MMExpandedListView) findViewById(R.id.elvsearchnocategory);
		MMExpandedListView elvSearchCategory = (MMExpandedListView) findViewById(R.id.elvsearchcategory);
		
		etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				Log.d(TAG, TAG + "actionId: " + actionId);
				if(actionId == EditorInfo.IME_ACTION_SEARCH) {
					searchCategory = etSearch.getText().toString();
					checkForGPS(MMAPIConstants.REQUEST_CODE_TURN_ON_GPS_SEARCH_TEXT);
				}
				return true;
			}
		});
		
		getSearchNoCategoryIcons();
		ArrayAdapter<Object> arrayAdapter = new MMArrayAdapter(SearchScreen.this, R.layout.mm_listview_row, 
				categoryIcons, getResources().getStringArray(R.array.lv_search_nocategory), categoryIndicatorIcons, 
				android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD, null);
		elvSearchNoCategory.setAdapter(arrayAdapter);
		elvSearchNoCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				searchCategory = ((TextView) view.findViewById(R.id.tvlabel)).getText().toString();
				if(position == 0) {				
					checkForGPS(MMAPIConstants.REQUEST_CODE_TURN_ON_GPS_SEARCH_ALL_NEARBY);
				} else if(position == 1) {
					checkForGPS(MMAPIConstants.REQUEST_CODE_TURN_ON_GPS_HISTORY);
				}
			}
		});
		
		getSearchCategoryIcons();
		arrayAdapter = new MMArrayAdapter(SearchScreen.this, R.layout.mm_listview_row, categoryIcons, 
				getTopLevelCategories(), new int[0], android.R.style.TextAppearance_Medium, 
				Typeface.DEFAULT_BOLD, null);
		elvSearchCategory.setAdapter(arrayAdapter);
		elvSearchCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				try { //TODO:Change this implementation so that it does not go to subcats
					//String catId = topLevelCategories.getJSONObject(position).getString(MMAPIConstants.JSON_KEY_CATEGORY_ID);
					LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					Double latitudeValue = location.getLatitude();
					Double longitudeValue = location.getLongitude();
					
					selectedCategory = topLevelCategories[position];
					JSONArray subCategories = new JSONArray(MMCategories.getSubCategoriesWithCategoryName(SearchScreen.this.getApplicationContext(), selectedCategory));
					
					if(!subCategories.isNull(0))
					{	
						Intent categoryScreenIntent = new Intent(SearchScreen.this, CategoryScreen.class);					
						categoryScreenIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_CATEGORY, (String) subCategories.toString());
						categoryScreenIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, selectedCategory);
						startActivity(categoryScreenIntent);
					}
					else if(userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES))
					{
						JSONObject cats = new JSONObject(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, MMAPIConstants.DEFAULT_STRING));
						progressDialog = ProgressDialog.show(SearchScreen.this, MMAPIConstants.DEFAULT_STRING, "Locating " + topLevelCategories[position]);
						String catId = cats.getJSONArray(topLevelCategories[position]).getJSONObject(0).getString(MMAPIConstants.JSON_KEY_CATEGORY_ID);
						MMSearchLocationAdapter.searchLocationWithText(
								new MMSearchResultsCallback(SearchScreen.this, progressDialog, location, topLevelCategories[position]), 
								Double.toString(longitudeValue), 
								Double.toString(latitudeValue), 
								userPrefs.getInt(MMAPIConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMAPIConstants.SEARCH_RADIUS_HALF_MILE), 
								MMAPIConstants.DEFAULT_STRING,
								cats.getJSONArray(topLevelCategories[position]).getJSONObject(0).getString(MMAPIConstants.JSON_KEY_CATEGORY_ID),
								userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
								userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), 
								MMConstants.PARTNER_ID);
					}
				} catch (JSONException e) { 
					e.printStackTrace();
				}
			}
		});
	}
	
	private void searchLocation(int requestCode) {
		switch(requestCode) {
			case MMAPIConstants.REQUEST_CODE_TURN_ON_GPS_ADD_LOCATION:
				startAddLocationMapScreen();
				break;
			case MMAPIConstants.REQUEST_CODE_TURN_ON_GPS_SEARCH_ALL_NEARBY:
				searchAllNearbyLocations();
				break;
			case MMAPIConstants.REQUEST_CODE_TURN_ON_GPS_SEARCH_TEXT:
				searchByText();
				break;
			case MMAPIConstants.REQUEST_CODE_TURN_ON_GPS_HISTORY:
				showHistory();
				break;
		}
	}
	
	private void startAddLocationMapScreen() {
		Intent addLocMapIntent = new Intent(SearchScreen.this, AddLocationMapScreen.class);
		addLocMapIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION, location);
		startActivity(addLocMapIntent);
	}
	
	private void searchAllNearbyLocations() {
		Log.d(TAG, TAG + "search radius: " + userPrefs.getInt(MMAPIConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMAPIConstants.SEARCH_RADIUS_HALF_MILE));
		
		MMSearchLocationAdapter.searchAllNearby(new SearchCallback(), Double.toString(longitudeValue), Double.toString(latitudeValue), 
				userPrefs.getInt(MMAPIConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMAPIConstants.SEARCH_RADIUS_HALF_MILE), userPrefs.getString(MMAPIConstants.KEY_USER, 
				MMAPIConstants.DEFAULT_STRING), userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), MMConstants.PARTNER_ID);
		progressDialog = ProgressDialog.show(SearchScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_search_all_nearby), true, false);
	}
	
	private void searchByText() {
		MMSearchLocationAdapter.searchLocationWithText(
				new SearchCallback(), 
				Double.toString(longitudeValue), 
				Double.toString(latitudeValue), 
				userPrefs.getInt(MMAPIConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMAPIConstants.SEARCH_RADIUS_HALF_MILE), 
				searchCategory,
				"",
				userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
				userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), 
				MMConstants.PARTNER_ID);
				progressDialog = ProgressDialog.show(SearchScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_search_for) + MMAPIConstants.DEFAULT_SPACE + 
				searchCategory + getString(R.string.pd_ellipses), true, false);
	}
	
	private void showHistory() {
		Intent searchResultsIntent = new Intent(SearchScreen.this, SearchResultsScreen.class);
		searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_DISPLAY_MAP, false);
		searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION, location);
		searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, searchCategory);
		searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS, userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_HISTORY, MMAPIConstants.DEFAULT_STRING));
		startActivity(searchResultsIntent);
	}
	
	private String[] getTopLevelCategories() throws JSONException {
//		String[] topLevelCats = new String[topLevelCategories.length()];
//		
//		for(int i = 0; i < topLevelCategories.length(); i++) {
//			topLevelCats[i] = topLevelCategories.getJSONObject(i).getString(Locale.getDefault().getLanguage());
//		}
		
		return topLevelCategories;
	}
	
	private void getSearchNoCategoryIcons() {
		categoryIcons = new int[]{R.drawable.cat_icon_show_all_nearby, R.drawable.cat_icon_history};
		categoryIndicatorIcons = new int[]{R.drawable.listview_accessory_indicator, R.drawable.listview_accessory_indicator};				
	}
	
	private void getSearchCategoryIcons() {
		categoryIcons = new int[] {
			R.drawable.cat_icon_beaches, 
			R.drawable.cat_icon_conferences, 
			R.drawable.cat_icon_restaurants, 
			R.drawable.cat_icon_dog_parks, 
			R.drawable.cat_icon_stadiums, 
			R.drawable.cat_icon_dog_parks, 
			R.drawable.cat_icon_coffee_shops, 
			R.drawable.cat_icon_schools, 
			R.drawable.cat_icon_supermarkets, 
			R.drawable.cat_icon_hotels,
			R.drawable.cat_icon_pubs,
			R.drawable.cat_icon_night_clubs,
			R.drawable.cat_icon_health_clubs,
			R.drawable.cat_icon_cinemas
		};
		categoryIndicatorIcons = new int[] {
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator
		};
	}
	
    /**
     * Custom {@link MMCallback} specifically for {@link SearchScreen} to be processed after receiving response from MobMonkey server.
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
				searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_DISPLAY_MAP, true);
				searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION, location);
				searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, searchCategory);
				searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS, (String) obj);
				startActivity(searchResultsIntent);
			}
			Log.d(TAG, TAG + "response: " + (String) obj);
		}
	}
}
