package com.mobmonkey.mobmonkey;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.mobmonkey.mobmonkey.utils.ExpandedListView;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMSearchCategoryArrayAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMHashMap;
import com.mobmonkey.mobmonkeyapi.adapters.MMSearchLocationAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchScreen extends Activity {
	private static final String TAG = "SearchScreen: ";
	SharedPreferences userPrefs;
	
	Location location;
	double longitudeValue;
	double latitudeValue;
	
	String searchCategory;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		setContentView(R.layout.search_screen);
		
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		longitudeValue = location.getLongitude();
		latitudeValue = location.getLatitude();
		DecimalFormat twoDForm = new DecimalFormat("#.######");
		latitudeValue = Double.valueOf(twoDForm.format(latitudeValue));
		longitudeValue = Double.valueOf(twoDForm.format(longitudeValue));
		
		Log.d(TAG, TAG + "LOCATION: Longitude: " + longitudeValue + " Latitude: " + latitudeValue);
		
		int[] categoryIcons = new int[]{R.drawable.icon_search, 
										R.drawable.icon_search};
		ExpandedListView lvSearchNoCategory = (ExpandedListView) findViewById(R.id.elvsearchnocategory);
		ArrayAdapter<Object> arrayAdapter = new MMSearchCategoryArrayAdapter(SearchScreen.this, R.layout.search_category_list_row, categoryIcons, R.array.search_nocategory);
		lvSearchNoCategory.setAdapter(arrayAdapter);
		lvSearchNoCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				if(position == 0) {				
					searchCategory = ((TextView) view.findViewById(R.id.tvcategory)).getText().toString();
					
					HashMap<String, Object> hashMap = MMHashMap.getInstance(MMConstants.PARTNER_ID);
					hashMap.put(MMAPIConstants.KEY_USER, userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING));
					hashMap.put(MMAPIConstants.KEY_AUTH, userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
					hashMap.put(MMAPIConstants.KEY_LATITUDE, Double.toString(latitudeValue));
					hashMap.put(MMAPIConstants.KEY_LONGITUDE, Double.toString(longitudeValue));
					
					MMSearchLocationAdapter.searchAllNearby(new SearchCallback(), hashMap);	
				} else {
					// TODO:
				}
			}
		});
		
		categoryIcons = new int[]{R.drawable.icon_search, 
								  R.drawable.icon_search, 
								  R.drawable.icon_search, 
								  R.drawable.icon_search, 
								  R.drawable.icon_search, 
								  R.drawable.icon_search, 
								  R.drawable.icon_search, 
								  R.drawable.icon_search, 
								  R.drawable.icon_search, 
								  R.drawable.icon_search};
		ExpandedListView lvSearchCategory = (ExpandedListView) findViewById(R.id.elvsearchcategory);
		arrayAdapter = new MMSearchCategoryArrayAdapter(SearchScreen.this, R.layout.search_category_list_row, categoryIcons, R.array.search_category);
		lvSearchCategory.setAdapter(arrayAdapter);
	}
	
	private class SearchCallback implements MMCallback {
		public void processCallback(Object obj) {
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
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
		return;
	}
}
