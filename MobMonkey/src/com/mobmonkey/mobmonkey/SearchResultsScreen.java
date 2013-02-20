/**
 * 
 */
package com.mobmonkey.mobmonkey;

import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.mobmonkey.mobmonkey.utils.MMResultsLocation;
import com.mobmonkey.mobmonkey.utils.MMSearchResultsArrayAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchResultsScreen extends MapActivity implements AdapterView.OnItemClickListener {
	private static final String TAG = "SearchResultsScreen: ";
	
	JSONArray searchResults;
	MMResultsLocation[] locations;
	SharedPreferences userPrefs;
	SharedPreferences.Editor userPrefsEditor;
	JSONArray history;
	
	TextView tvSearchResultsTitle;
	ListView lvSearchResults;
	MapView mvLocationsResult;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_results_screen);
		
		try {
			init();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		try {
			Log.d(TAG, TAG + "onItemClick");
			addToHistory(position);
			
			Intent locDetailsIntent = new Intent(SearchResultsScreen.this, SearchResultDetailsScreen.class);
			locDetailsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, searchResults.getJSONObject(position).toString());
			startActivity(locDetailsIntent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.ibmap:
				if(lvSearchResults.getVisibility() == View.VISIBLE) {
					lvSearchResults.setVisibility(View.INVISIBLE);
					mvLocationsResult.setVisibility(View.VISIBLE);
				} else if(lvSearchResults.getVisibility() == View.INVISIBLE) {
					lvSearchResults.setVisibility(View.VISIBLE);
					mvLocationsResult.setVisibility(View.INVISIBLE);
				}
				break;
			case R.id.ibaddlocation:
				break;
		}
	}
	
	private void init() throws JSONException {
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		tvSearchResultsTitle = (TextView) findViewById(R.id.tvsearchresultstitle);
		lvSearchResults = (ListView) findViewById(R.id.lvsearchresults);
		mvLocationsResult = (MapView) findViewById(R.id.mvlocationsresult);
		
		tvSearchResultsTitle.setText(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE));
		
		searchResults = new JSONArray(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS));
		getLocations();
		
		ArrayAdapter<MMResultsLocation> arrayAdapter = new MMSearchResultsArrayAdapter(SearchResultsScreen.this, R.layout.search_result_list_row, locations);
		lvSearchResults.setAdapter(arrayAdapter);
		lvSearchResults.setOnItemClickListener(SearchResultsScreen.this);
		
		if(userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_HISTORY)) {
			history = new JSONArray(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_HISTORY, MMAPIConstants.DEFAULT_STRING));
		} else {
			history = new JSONArray();
		}
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void getLocations() throws JSONException {
			locations = new MMResultsLocation[searchResults.length()];
			for(int i = 0; i < searchResults.length(); i++) {
				JSONObject jObj = searchResults.getJSONObject(i);
				locations[i] = new MMResultsLocation();
				locations[i].setLocName(jObj.getString(MMAPIConstants.JSON_KEY_NAME));
				locations[i].setLocDist(convertMetersToMiles(jObj.getString(MMAPIConstants.JSON_KEY_DISTANCE)) + getString(R.string.miles));
				locations[i].setLocAddr(jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS) + MMAPIConstants.DEFAULT_NEWLINE + jObj.getString(MMAPIConstants.JSON_KEY_LOCALITY) + MMAPIConstants.COMMA_SPACE + 
										jObj.getString(MMAPIConstants.JSON_KEY_REGION) + MMAPIConstants.COMMA_SPACE + jObj.getString(MMAPIConstants.JSON_KEY_POSTCODE));
			}
	}
	
	/**
	 * 
	 * @param distance
	 * @return
	 */
	private String convertMetersToMiles(String distance) {
		double dist = Double.valueOf(distance);
		
		dist = dist * 0.000621371f;
		
		return new DecimalFormat("#.##").format(dist) + MMAPIConstants.DEFAULT_SPACE;
	}
	
	/**
	 * 
	 * @param position
	 * @throws JSONException
	 */
	private void addToHistory(int position) throws JSONException {
		JSONObject loc = searchResults.getJSONObject(position);
		Log.d(TAG, TAG + "loc: " + loc);
		if(!locationExistsInHistory(loc)) {
			if(history.length() < 10) {
				history.put(loc);
			} else {
				for(int i = 0; i < 8; i++) {
					history.put(i, history.get(i+1));
				}
				history.put(9, loc);
			}
			userPrefsEditor.putString(MMAPIConstants.SHARED_PREFS_KEY_HISTORY, history.toString());
			userPrefsEditor.commit();
		}
	}
	
	/**
	 * 
	 * @param loc
	 * @return
	 * @throws JSONException
	 */
	private boolean locationExistsInHistory(JSONObject loc) throws JSONException {
		Log.d(TAG, TAG + "history length: " + history.length());
		
		if(history.length() <= 0) {
			return false;
		}
		
		// TODO: Search through history and see if the loc exists, return true if it does
		for(int i=0; i<history.length(); i++)
		{
			if(history.getJSONObject(i).getString("name").equals(loc.getString("name")))
				return true;
		}
		
		return false;
	}
}
