package com.mobmonkey.mobmonkey;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.mobmonkey.mobmonkey.utils.MMResultsLocation;
import com.mobmonkey.mobmonkey.utils.MMSearchResultsArrayAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchResultsScreen extends FragmentActivity implements AdapterView.OnItemClickListener, OnInfoWindowClickListener {
	private static final String TAG = "SearchResultsScreen: ";
	
	JSONArray searchResults;
	Location location;
	MMResultsLocation[] locations;
	SharedPreferences userPrefs;
	SharedPreferences.Editor userPrefsEditor;
	JSONArray locationHistory;
	
	TextView tvSearchResultsTitle;
	ListView lvSearchResults;
	SupportMapFragment smfResultLocations;
	GoogleMap googleMap;
	
	HashMap<Marker, JSONObject> markerHashMap;
	Intent locDetailsIntent;
	
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
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		try {
			Log.d(TAG, TAG + "onItemClick");
			addToHistory(position);
			
			locDetailsIntent = new Intent(SearchResultsScreen.this, SearchResultDetailsScreen.class);
			locDetailsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION, location);
			locDetailsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, searchResults.getJSONObject(position).toString());
			startActivity(locDetailsIntent);
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
		Log.d(TAG, TAG + "marker: " + jObj.toString());
		
		locDetailsIntent = new Intent(SearchResultsScreen.this, SearchResultDetailsScreen.class);
		locDetailsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION, location);
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
				if(lvSearchResults.getVisibility() == View.VISIBLE) {
					lvSearchResults.setVisibility(View.INVISIBLE);
					smfResultLocations.getView().setVisibility(View.VISIBLE);
				} else if(lvSearchResults.getVisibility() == View.INVISIBLE) {
					lvSearchResults.setVisibility(View.VISIBLE);
					smfResultLocations.getView().setVisibility(View.INVISIBLE);
				}
				break;
			case R.id.ibaddlocation:
				break;
		}
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void init() throws JSONException {
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		tvSearchResultsTitle = (TextView) findViewById(R.id.tvsearchresultstitle);
		lvSearchResults = (ListView) findViewById(R.id.lvsearchresults);
		smfResultLocations = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmap);
		googleMap = smfResultLocations.getMap();
		markerHashMap = new HashMap<Marker, JSONObject>();
		
		searchResults = new JSONArray(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS));
		location = getIntent().getParcelableExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION);
		
		getLocations();
		
		tvSearchResultsTitle.setText(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE));	
		smfResultLocations.getView().setVisibility(View.INVISIBLE);
		
		ArrayAdapter<MMResultsLocation> arrayAdapter = new MMSearchResultsArrayAdapter(SearchResultsScreen.this, R.layout.search_result_list_row, locations);
		lvSearchResults.setAdapter(arrayAdapter);
		lvSearchResults.setOnItemClickListener(SearchResultsScreen.this);
		
		addToGoogleMap();
		
		if(userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_HISTORY)) {
			locationHistory = new JSONArray(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_HISTORY, MMAPIConstants.DEFAULT_STRING));
		} else {
			locationHistory = new JSONArray();
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
				locations[i].setLocDist(calcDist(jObj.getDouble(MMAPIConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMAPIConstants.JSON_KEY_LONGITUDE)) + getString(R.string.miles));
				locations[i].setLocAddr(jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS) + MMAPIConstants.DEFAULT_NEWLINE + jObj.getString(MMAPIConstants.JSON_KEY_LOCALITY) + MMAPIConstants.COMMA_SPACE + 
										jObj.getString(MMAPIConstants.JSON_KEY_REGION) + MMAPIConstants.COMMA_SPACE + jObj.getString(MMAPIConstants.JSON_KEY_POSTCODE));
			}
	}
	
	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	private String calcDist(double latitude, double longitude) {
		Location resultLocation = new Location(location);
		resultLocation.setLatitude(latitude);
		resultLocation.setLongitude(longitude);
		
		return convertMetersToMiles(location.distanceTo(resultLocation));
	}
	
	/**
	 * 
	 * @param dist
	 * @return
	 */
	private String convertMetersToMiles(double dist) {
		dist = dist * 0.000621371f;
		
		return new DecimalFormat("#.##").format(dist) + MMAPIConstants.DEFAULT_SPACE;
	}
	
	private void addToGoogleMap() throws JSONException {		
		for(int i = 0; i < searchResults.length(); i++) {
			JSONObject jObj = searchResults.getJSONObject(i);
			
			LatLng resultLocLatLng = new LatLng(jObj.getDouble(MMAPIConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMAPIConstants.JSON_KEY_LONGITUDE));
			
			Marker locationResultMarker = googleMap.addMarker(new MarkerOptions().
					position(resultLocLatLng).
					title(jObj.getString(MMAPIConstants.JSON_KEY_NAME))
					.snippet(jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS)));
			
			markerHashMap.put(locationResultMarker, jObj);
		}
		
		LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 16));
		googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		googleMap.setOnInfoWindowClickListener(SearchResultsScreen.this);
		googleMap.setMyLocationEnabled(true);
	}
	
	/**
	 * 
	 * @param position
	 * @throws JSONException
	 */
	private void addToHistory(int position) throws JSONException {
		JSONObject loc = searchResults.getJSONObject(position);

		if(!locationExistsInHistory(loc)) {
			if(locationHistory.length() < 10) {
				locationHistory.put(loc);
			} else {
				for(int i = 0; i < 8; i++) {
					locationHistory.put(i, locationHistory.get(i+1));
				}
				locationHistory.put(9, loc);
			}
			userPrefsEditor.putString(MMAPIConstants.SHARED_PREFS_KEY_HISTORY, locationHistory.toString());
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
		if(locationHistory.length() <= 0) {
			return false;
		}
		
		for(int i = 0; i < locationHistory.length(); i++) {
			if(locationHistory.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_LATITUDE).equals(loc.getString(MMAPIConstants.JSON_KEY_LATITUDE)) &&
					locationHistory.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_LONGITUDE).equals(loc.getString(MMAPIConstants.JSON_KEY_LONGITUDE))) {
				return true;
			}
		}
		
		return false;
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
