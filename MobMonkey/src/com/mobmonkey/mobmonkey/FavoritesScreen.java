package com.mobmonkey.mobmonkey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import com.mobmonkey.mobmonkey.utils.MMUtility;
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
public class FavoritesScreen extends FragmentActivity implements AdapterView.OnItemClickListener, OnInfoWindowClickListener {
	private static final String TAG = "FavoritesScreen: ";
	
	private SharedPreferences userPrefs;
	
	Location location;
	
	private ListView lvFavorites;
	private SupportMapFragment smfFavoriteLocations;
	private GoogleMap googleMap;
	HashMap<Marker, JSONObject> markerHashMap;
	
	private MMResultsLocation[] favoriteLocations;
	private JSONArray bookmarksList;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmarks_screen);
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		refreshFavorites();
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
		try {
			Intent favLocDetailsIntent = new Intent(FavoritesScreen.this, SearchResultDetailsScreen.class);
			favLocDetailsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, bookmarksList.getJSONObject(position).toString());
			startActivity(favLocDetailsIntent);
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
		
		Intent favLocDetailsIntent = new Intent(FavoritesScreen.this, SearchResultDetailsScreen.class);
		favLocDetailsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, jObj.toString());
		startActivity(favLocDetailsIntent);
	}
	
	/**
	 * 
	 * @param view
	 */
	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.ibmap:
				if(MMLocationManager.isGPSEnabled()) {
					if(lvFavorites.getVisibility() == View.VISIBLE) {
						lvFavorites.setVisibility(View.INVISIBLE);
						smfFavoriteLocations.getView().setVisibility(View.VISIBLE);
					} else if(lvFavorites.getVisibility() == View.INVISIBLE) {
						lvFavorites.setVisibility(View.VISIBLE);
						smfFavoriteLocations.getView().setVisibility(View.INVISIBLE);
					}
				}
				break;
			case R.id.btnaddloc:
				if(MMLocationManager.isGPSEnabled()) {
					startActivity(new Intent(FavoritesScreen.this, AddLocationScreen.class));
				}
				break;
		}
	}
	
	private void init() {
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		lvFavorites = (ListView) findViewById(R.id.lvbookmarks);
		smfFavoriteLocations = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmap);
		
		googleMap = smfFavoriteLocations.getMap();
		markerHashMap = new HashMap<Marker, JSONObject>();
		
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		
		onCreateBookmarks();
	}
	
	/**
	 * Make a call to the server and refresh the Favorites list
	 */
	private void refreshFavorites() {
		MMBookmarksAdapter.getBookmarks(new FavoritesCallback(), 
										MMAPIConstants.URL_BOOKMARKS, 
										MMConstants.PARTNER_ID, 
										userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
										userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
	}
	
	private void onCreateBookmarks() {
		try {
			bookmarksList = new JSONArray(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_BOOKMARKS, MMAPIConstants.DEFAULT_STRING));
			getFavorites();
			ArrayAdapter<MMResultsLocation> arrayAdapter 
				= new MMSearchResultsArrayAdapter(FavoritesScreen.this, R.layout.search_result_list_row, favoriteLocations);
			lvFavorites.setAdapter(arrayAdapter);
			
			arrayAdapter.notifyDataSetChanged();
			
			lvFavorites.setOnItemClickListener(FavoritesScreen.this);
			
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
		
		LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 16));
		googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		googleMap.setOnInfoWindowClickListener(FavoritesScreen.this);
		googleMap.setMyLocationEnabled(true);
	}
	
	private void getFavorites() throws JSONException {
		favoriteLocations = new MMResultsLocation[bookmarksList.length()];
		for(int i = 0; i < bookmarksList.length(); i++) {
			JSONObject jObj = bookmarksList.getJSONObject(i);
			favoriteLocations[i] = new MMResultsLocation();
			favoriteLocations[i].setLocName(jObj.getString(MMAPIConstants.JSON_KEY_NAME));
			favoriteLocations[i].setLocDist(MMUtility.calcDist(location, jObj.getDouble(MMAPIConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMAPIConstants.JSON_KEY_LONGITUDE)) + MMAPIConstants.DEFAULT_SPACE + getString(R.string.miles));
			favoriteLocations[i].setLocAddr(jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS) + MMAPIConstants.DEFAULT_NEWLINE + jObj.getString(MMAPIConstants.JSON_KEY_LOCALITY) + MMAPIConstants.COMMA_SPACE + 
									jObj.getString(MMAPIConstants.JSON_KEY_REGION) + MMAPIConstants.COMMA_SPACE + jObj.getString(MMAPIConstants.JSON_KEY_POSTCODE));
		}
		
		// reverse array
		List temp = Arrays.asList(favoriteLocations);
		Collections.reverse(temp);
		favoriteLocations = (MMResultsLocation[]) temp.toArray();
		
		temp = new ArrayList<JSONObject>();
		for(int i = 0; i < bookmarksList.length(); i++) {
			temp.add(bookmarksList.get(i));
		}
		Collections.reverse(temp);
		bookmarksList = new JSONArray(temp);
	}
	
	// callback for bookmark list	
	private class FavoritesCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				try {
					Log.d(TAG, TAG + "response: " + ((String) obj));
					bookmarksList = new JSONArray((String) obj);
					if(MMLocationManager.isGPSEnabled()) {
						getFavorites();
						ArrayAdapter<MMResultsLocation> arrayAdapter = new MMSearchResultsArrayAdapter(FavoritesScreen.this, R.layout.search_result_list_row, favoriteLocations);
						lvFavorites.setAdapter(arrayAdapter);
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
