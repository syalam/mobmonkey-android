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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
public class SearchScreen extends Activity {
	private static final String TAG = "SearchScreen: ";
	SharedPreferences userPrefs;
	
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
		
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		longitudeValue = location.getLongitude();
		latitudeValue = location.getLatitude();
		DecimalFormat twoDForm = new DecimalFormat("#.######");
		latitudeValue = Double.valueOf(twoDForm.format(latitudeValue));
		longitudeValue = Double.valueOf(twoDForm.format(longitudeValue));
		
		Log.d(TAG, TAG + "LOCATION: Longitude: " + longitudeValue + " Latitude: " + latitudeValue);
		
		etSearch = (EditText) findViewById(R.id.etsearch);
		etSearch.setImeActionLabel("Search", KeyEvent.KEYCODE_SEARCH);
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
		
		int[] categoryIcons = new int[]{R.drawable.icon_search, 
										R.drawable.icon_search};
		
		ExpandedListView lvSearchNoCategory = (ExpandedListView) findViewById(R.id.elvsearchnocategory);
		ArrayAdapter<Object> arrayAdapter = new MMArrayAdapter(SearchScreen.this, R.layout.expanded_listview_row, categoryIcons, getResources().getStringArray(R.array.search_nocategory), android.R.style.TextAppearance_Large, Typeface.DEFAULT_BOLD);
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
					progressDialog = ProgressDialog.show(SearchScreen.this, MMAPIConstants.DEFAULT_STRING, "searching nearby...", true, false);
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
		
		ExpandedListView lvSearchCategory = (ExpandedListView) findViewById(R.id.elvsearchcategory);
		arrayAdapter = new MMArrayAdapter(SearchScreen.this, R.layout.expanded_listview_row, categoryIcons, getResources().getStringArray(R.array.search_category), android.R.style.TextAppearance_Large, Typeface.DEFAULT_BOLD);
		lvSearchCategory.setAdapter(arrayAdapter);
	}
	
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
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
		return;
	}
}
