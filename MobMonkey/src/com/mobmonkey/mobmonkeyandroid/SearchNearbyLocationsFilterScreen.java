package com.mobmonkey.mobmonkeyandroid;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMNearbyLocationsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMSearchResultsItem;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedNearbyLocationsListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchNearbyLocationsFilterScreen extends Activity implements OnItemClickListener {
	private static final String TAG = "SearchNearbyLocationsFilterScreen: ";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	
	private Location location;
	private JSONArray nearbyLocations;
	private JSONArray locationHistory;
	
	private LinearLayout llCreateHotSpot;
	private EditText etSearchNearbyLocations;
	private LinearLayout llNearbyLocations;
	private ListView lvNearbyLocations;
	
	private MMNearbyLocationsArrayAdapter nearbyLocationsArrayAdapter;
	private InputMethodManager inputMethodManager;
	
	private int nearbyLocationsCount = 5;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_nearby_locations_filter_screen);
		init();
	}
	
	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		try {
			addToHistory(nearbyLocations.getJSONObject(position));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void viewOnClick(View view) {
		Log.d(TAG, TAG + "view id: " + view.getId());
		
		switch(view.getId()) {
			case R.id.btncancel:
				setResult(Activity.RESULT_CANCELED);
				finish();
				Log.d(TAG, TAG + "finish");
				break;
		}
	}
	
	private void init() {
		userPrefs = getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		etSearchNearbyLocations = (EditText) findViewById(R.id.etsearch);
		llNearbyLocations = (LinearLayout) findViewById(R.id.llnearbylocations);
		lvNearbyLocations = (ListView) findViewById(R.id.lvnearbylocations);
		
		etSearchNearbyLocations.addTextChangedListener(new NearbyLocationsTextWatcher());
		lvNearbyLocations.setOnItemClickListener(SearchNearbyLocationsFilterScreen.this);
		
		try {
			String nearbyLocs = getIntent().getStringExtra(MMSDKConstants.KEY_INTENT_EXTRA_NEARBY_LOCATIONS);
			Log.d(TAG, TAG + "nearbyLocs: " + nearbyLocs);
			if(!nearbyLocs.equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
				nearbyLocations = new JSONArray(nearbyLocs);
			} else {
				nearbyLocations = new JSONArray();
			}
			setNearbyLocations();
			getLocationHistory();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws JSONException
	 */
	private boolean getLocationHistory() throws JSONException {
		String history = userPrefs.getString(MMSDKConstants.SHARED_PREFS_KEY_HISTORY, MMSDKConstants.DEFAULT_STRING_EMPTY);
		if(!history.equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
			locationHistory = new JSONArray(history);
			return true;
		} else {
			locationHistory = new JSONArray();
			return false;
		}
	}
	
	/**
	 * 
	 * @param position
	 * @throws JSONException
	 */
	private void addToHistory(JSONObject loc) throws JSONException {
		if(!locationExistsInHistory(loc)) {
			if(locationHistory.length() < MMSDKConstants.HISTORY_SIZE) {
				ArrayList<JSONObject> temp = new ArrayList<JSONObject>();
				//Convert to ArrayList so that you can add last item view to front of array
				for (int i=0; i<locationHistory.length(); i++)
					temp.add(locationHistory.getJSONObject(i));
				temp.add(0, loc);
				locationHistory = new JSONArray(temp);
			} else {
				ArrayList<JSONObject> temp = new ArrayList<JSONObject>();
				//Convert to ArrayList so that you can add last item view to front of array
				for (int i=0; i<locationHistory.length(); i++)
					temp.add(locationHistory.getJSONObject(i));
				temp.add(0, loc);
				temp.remove(MMSDKConstants.HISTORY_SIZE);
				locationHistory = new JSONArray(temp);
			}
		}
		userPrefsEditor.putString(MMSDKConstants.SHARED_PREFS_KEY_HISTORY, locationHistory.toString());
		userPrefsEditor.commit();
	}
	
	/**
	 * 
	 * @param loc
	 * @return
	 * @throws JSONException
	 */
	private boolean locationExistsInHistory(JSONObject loc) throws JSONException {
		if(locationHistory.length() <= 0) {
			return false;
		}
		
		for(int i = 0; i < locationHistory.length(); i++) {
			if(locationHistory.getJSONObject(i).getString(MMSDKConstants.JSON_KEY_NAME).equals(loc.getString(MMSDKConstants.JSON_KEY_NAME)) &&
					locationHistory.getJSONObject(i).getString(MMSDKConstants.JSON_KEY_LATITUDE).equals(loc.getString(MMSDKConstants.JSON_KEY_LATITUDE)) &&
					locationHistory.getJSONObject(i).getString(MMSDKConstants.JSON_KEY_LONGITUDE).equals(loc.getString(MMSDKConstants.JSON_KEY_LONGITUDE))) {
				ArrayList<JSONObject> temp = new ArrayList<JSONObject>();
				//Convert to ArrayList so that you can add last item view to front of array
				for (int j=0; j<locationHistory.length(); j++)
					temp.add(locationHistory.getJSONObject(j));
				JSONObject tempObj = temp.remove(i);
				temp.add(0, tempObj);
				locationHistory = new JSONArray(temp);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void setNearbyLocations() throws JSONException {
		ArrayList<MMSearchResultsItem> resultLocations = new ArrayList<MMSearchResultsItem>();
		for(int i = 0; i < nearbyLocations.length(); i++) {
			JSONObject jObj = nearbyLocations.getJSONObject(i);
			MMSearchResultsItem searchResultsItem = new MMSearchResultsItem();
			searchResultsItem.setLocName(jObj.getString(MMSDKConstants.JSON_KEY_NAME));
			searchResultsItem.setLocDist(MMUtility.calcDist(location, jObj.getDouble(MMSDKConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE)) + MMSDKConstants.DEFAULT_STRING_SPACE + 
					getString(R.string.miles));
			searchResultsItem.setLocAddr(jObj.getString(MMSDKConstants.JSON_KEY_ADDRESS) + MMSDKConstants.DEFAULT_STRING_NEWLINE + jObj.getString(MMSDKConstants.JSON_KEY_LOCALITY) + MMSDKConstants.DEFAULT_STRING_COMMA_SPACE + 
									jObj.getString(MMSDKConstants.JSON_KEY_REGION));
			resultLocations.add(searchResultsItem);
			Log.d(TAG, i + " stream: " + jObj.getInt(MMSDKConstants.MEDIA_LIVESTREAMING) + " video: " + jObj.getInt(MMSDKConstants.JSON_KEY_VIDEOS) + " images: " + jObj.getInt(MMSDKConstants.JSON_KEY_IMAGES));
		}
		
		nearbyLocationsArrayAdapter = new MMNearbyLocationsArrayAdapter(SearchNearbyLocationsFilterScreen.this, R.layout.listview_row_searchresults, resultLocations);
//		nearbyLocationsArrayAdapter.setLocationsToDisplay(nearbyLocationsCount);
		lvNearbyLocations.setAdapter(nearbyLocationsArrayAdapter);
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class NearbyLocationsTextWatcher implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(nearbyLocationsArrayAdapter != null) {
				if(s.length() > 0) {
					llNearbyLocations.setVisibility(View.VISIBLE);
					nearbyLocationsArrayAdapter.getFilter().filter(s);
				} else {
					llNearbyLocations.setVisibility(View.GONE);
				}
			}
		}
	}
}
