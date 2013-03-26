package com.mobmonkey.mobmonkey;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMResultsLocation;
import com.mobmonkey.mobmonkey.utils.MMSearchResultsArrayAdapter;
import com.mobmonkey.mobmonkeyapi.adapters.MMBookmarksAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

/**
 * Android {@link Activity} screen displays search locations for the user
 * @author Dezapp, LLC
 *
 */
public class BookmarksScreen extends FragmentActivity implements AdapterView.OnItemClickListener {

	private SharedPreferences userPrefs;
	private ListView lvBookmark;
	private MMResultsLocation[] locations;
	private JSONArray bookmarkList;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmarks_screen);
		
		lvBookmark = (ListView) findViewById(R.id.lvbookmarks);
		
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		
		onCreateBookmarks();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		refreshList();
	}

	private void refreshList() {
		
		// refresh bookmark list
		MMBookmarksAdapter.getBookmarks(new bookmarksCallback(), 
										MMAPIConstants.URL_BOOKMARKS, 
										MMConstants.PARTNER_ID, 
										userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
										userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
	}
	
	private void onCreateBookmarks() {
		try {
				bookmarkList = new JSONArray(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_BOOKMARKS, ""));
				getLocations();
				ArrayAdapter<MMResultsLocation> arrayAdapter 
					= new MMSearchResultsArrayAdapter(BookmarksScreen.this, R.layout.search_result_list_row, locations);
				lvBookmark.setAdapter(arrayAdapter);
				
				arrayAdapter.notifyDataSetChanged();
				
				lvBookmark.setOnItemClickListener(BookmarksScreen.this);
				
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
	}
	
	private void getLocations() throws JSONException {
		locations = new MMResultsLocation[bookmarkList.length()];
		for(int i = 0; i < bookmarkList.length(); i++) {
			JSONObject jObj = bookmarkList.getJSONObject(i);
			locations[i] = new MMResultsLocation();
			locations[i].setLocName(jObj.getString(MMAPIConstants.JSON_KEY_NAME));
			locations[i].setLocDist(calcDist(jObj.getDouble(MMAPIConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMAPIConstants.JSON_KEY_LONGITUDE)) + getString(R.string.miles));
			locations[i].setLocAddr(jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS) + MMAPIConstants.DEFAULT_NEWLINE + jObj.getString(MMAPIConstants.JSON_KEY_LOCALITY) + MMAPIConstants.COMMA_SPACE + 
									jObj.getString(MMAPIConstants.JSON_KEY_REGION) + MMAPIConstants.COMMA_SPACE + jObj.getString(MMAPIConstants.JSON_KEY_POSTCODE));
		}
		
		// reverse array
		List temp = Arrays.asList(locations);
		Collections.reverse(temp);
		locations = (MMResultsLocation[]) temp.toArray();
		
		temp = new ArrayList<JSONObject>();
		for(int i = 0; i < bookmarkList.length(); i++) {
			temp.add(bookmarkList.get(i));
		}
		Collections.reverse(temp);
		bookmarkList = new JSONArray(temp);
	}
	
	private String calcDist(double latitude, double longitude) {
		
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		Location resultLocation = new Location(location);
		resultLocation.setLatitude(latitude);
		resultLocation.setLongitude(longitude);
		
		return convertMetersToMiles(location.distanceTo(resultLocation));
	}

	private String convertMetersToMiles(double dist) {
		dist = dist * 0.000621371f;
		
		return new DecimalFormat("#.##").format(dist) + MMAPIConstants.DEFAULT_SPACE;
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

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

		Log.d("BookmarksScreen", "onItemClick");
		try {
			//Log.d(TAG, TAG + "onItemClick");
			//addToHistory(searchResults.getJSONObject(position));
			
			LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			
			Intent intent = new Intent(BookmarksScreen.this, SearchResultDetailsScreen.class);
			
			intent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION, location);
			intent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, bookmarkList.getJSONObject(position).toString());
			startActivity(intent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	// callback for bookmark list
	
	private class bookmarksCallback implements MMCallback {

		@Override
		public void processCallback(Object obj) {
			
			if(obj != null) {
				try {
					bookmarkList = new JSONArray((String) obj);
					getLocations();
					ArrayAdapter<MMResultsLocation> arrayAdapter 
						= new MMSearchResultsArrayAdapter(BookmarksScreen.this, R.layout.search_result_list_row, locations);
					lvBookmark.setAdapter(arrayAdapter);
					
					//arrayAdapter.notifyDataSetChanged();
					
					lvBookmark.setOnItemClickListener(BookmarksScreen.this);
					
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
			}
		}
	}
}
