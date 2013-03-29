package com.mobmonkey.mobmonkey;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMResultsLocation;
import com.mobmonkey.mobmonkey.utils.MMSearchResultsArrayAdapter;
import com.mobmonkey.mobmonkeyapi.adapters.MMBookmarksAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationListener;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationManager;

/**
 * Android {@link Activity} screen displays search locations for the user
 * @author Dezapp, LLC
 *
 */
public class BookmarksScreen extends FragmentActivity implements AdapterView.OnItemClickListener, OnInfoWindowClickListener {
	private static final String TAG = "BookmarksScreen: ";
	
	private SharedPreferences userPrefs;
	
//	Location location;
	
	private ListView lvBookmarks;
	private SupportMapFragment smfBookmarkLocations;
	private GoogleMap googleMap;
	HashMap<Marker, JSONObject> markerHashMap;
	
	private MMResultsLocation[] locations;
	private JSONArray bookmarksList;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmarks_screen);
		
//		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//		location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		lvBookmarks = (ListView) findViewById(R.id.lvbookmarks);
		smfBookmarkLocations = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmap);
		
		googleMap = smfBookmarkLocations.getMap();
		markerHashMap = new HashMap<Marker, JSONObject>();
		
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		
		onCreateBookmarks();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		refreshList();
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
			intent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, bookmarksList.getJSONObject(position).toString());
			startActivity(intent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener#onInfoWindowClick(com.google.android.gms.maps.model.Marker)
	 */
	@Override
	public void onInfoWindowClick(Marker marker) {
		JSONObject jObj = markerHashMap.get((Marker) marker);
		
		Intent locDetailsIntent = new Intent(BookmarksScreen.this, SearchResultDetailsScreen.class);
		locDetailsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION, MMLocationManager.getGPSLocation(new MMLocationListener()));
		locDetailsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, jObj.toString());
		startActivity(locDetailsIntent);
	}
	
	/**
	 * 
	 * @param view
	 */
	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.ibmap:
				if(MMLocationManager.isGPSEnabled()) {
					if(lvBookmarks.getVisibility() == View.VISIBLE) {
						lvBookmarks.setVisibility(View.INVISIBLE);
						smfBookmarkLocations.getView().setVisibility(View.VISIBLE);
					} else if(lvBookmarks.getVisibility() == View.INVISIBLE) {
						lvBookmarks.setVisibility(View.VISIBLE);
						smfBookmarkLocations.getView().setVisibility(View.INVISIBLE);
					}
				}
				break;
			case R.id.btnaddloc:
				startActivity(new Intent(BookmarksScreen.this, AddLocationScreen.class));
				break;
		}
	}
	
	private void refreshList() {
		
		// refresh bookmark list
		MMBookmarksAdapter.getBookmarks(new BookmarksCallback(), 
										MMAPIConstants.URL_BOOKMARKS, 
										MMConstants.PARTNER_ID, 
										userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
										userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
	}
	
	private void onCreateBookmarks() {
		try {
				bookmarksList = new JSONArray(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_BOOKMARKS, MMAPIConstants.DEFAULT_STRING));
				getLocations();
				ArrayAdapter<MMResultsLocation> arrayAdapter 
					= new MMSearchResultsArrayAdapter(BookmarksScreen.this, R.layout.search_result_list_row, locations);
				lvBookmarks.setAdapter(arrayAdapter);
				
				arrayAdapter.notifyDataSetChanged();
				
				lvBookmarks.setOnItemClickListener(BookmarksScreen.this);
				
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
	}
	
	private void addToGoogleMap() throws JSONException {		
		for(int i = 0; i < bookmarksList.length(); i++) {
			JSONObject jObj = bookmarksList.getJSONObject(i);
			
			LatLng resultLocLatLng = new LatLng(jObj.getDouble(MMAPIConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMAPIConstants.JSON_KEY_LONGITUDE));
			
			Marker locationResultMarker = googleMap.addMarker(new MarkerOptions().
					position(resultLocLatLng).
					title(jObj.getString(MMAPIConstants.JSON_KEY_NAME))
					.snippet(jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS)));
			
			markerHashMap.put(locationResultMarker, jObj);
		}
		
		LatLng currentLoc = new LatLng(MMLocationManager.getGPSLocation(new MMLocationListener()).getLatitude(), MMLocationManager.getGPSLocation(new MMLocationListener()).getLongitude());
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 16));
		googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		googleMap.setOnInfoWindowClickListener(BookmarksScreen.this);
		googleMap.setMyLocationEnabled(true);
	}
	
	private void getLocations() throws JSONException {
		locations = new MMResultsLocation[bookmarksList.length()];
		for(int i = 0; i < bookmarksList.length(); i++) {
			JSONObject jObj = bookmarksList.getJSONObject(i);
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
		for(int i = 0; i < bookmarksList.length(); i++) {
			temp.add(bookmarksList.get(i));
		}
		Collections.reverse(temp);
		bookmarksList = new JSONArray(temp);
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
	
	// callback for bookmark list
	
	private class BookmarksCallback implements MMCallback {

		@Override
		public void processCallback(Object obj) {
			
			if(obj != null) {
				try {
					Log.d(TAG, TAG + "response: " + ((String) obj));
					bookmarksList = new JSONArray((String) obj);
					if(MMLocationManager.isGPSEnabled()) {
						getLocations();
						ArrayAdapter<MMResultsLocation> arrayAdapter = new MMSearchResultsArrayAdapter(BookmarksScreen.this, R.layout.search_result_list_row, locations);
						lvBookmarks.setAdapter(arrayAdapter);
						
						//arrayAdapter.notifyDataSetChanged();
						
						lvBookmarks.setOnItemClickListener(BookmarksScreen.this);
						addToGoogleMap();
					}
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class CustomInfoWindowAdapter implements InfoWindowAdapter {
        private final View mWindow;
        private final View mContents;

        public CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                titleUi.setText(title);
            } else {
                titleUi.setText(MMAPIConstants.DEFAULT_STRING);
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null) {
                snippetUi.setText(snippet);
            } else {
                snippetUi.setText(MMAPIConstants.DEFAULT_STRING);
            }
        }
    }
}
