package com.mobmonkey.mobmonkey.fragments;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.SearchResultDetailsScreen;
import com.mobmonkey.mobmonkey.SearchResultsScreen;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkey.utils.MMResultsLocation;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class FavoritesFragment extends MMFragment implements OnClickListener, OnInfoWindowClickListener {
	SharedPreferences userPrefs;
	Location location;
	MMResultsLocation[] locations;
	JSONArray favoritesList;
	
	ImageButton ibMap;
	Button btnAddLoc;
	ListView lvFavorites;
	SupportMapFragment smfResultLocations;
	GoogleMap googleMap;
	
	HashMap<Marker, JSONObject> markerHashMap;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, getActivity().MODE_PRIVATE);
		
		String favorites = userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_BOOKMARKS, MMAPIConstants.DEFAULT_STRING);
		if(favorites.equals(MMAPIConstants.DEFAULT_STRING)) {
			favoritesList = new JSONArray();
		} else {
			try {
				favoritesList = new JSONArray(favorites);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		View view = inflater.inflate(R.layout.fragment_favorites, container, false);
		
		ibMap = (ImageButton) view.findViewById(R.id.ibmap);
		btnAddLoc = (Button) view.findViewById(R.id.btnaddloc);
		smfResultLocations = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmap);
		
		// TODO: LocationManager
		location = null;
		googleMap = smfResultLocations.getMap();
		markerHashMap = new HashMap<Marker, JSONObject>();
		
		lvFavorites = (ListView) view.findViewById(R.id.lvbookmarks);
		
		try {
			getLocations();
			addToGoogleMap();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		ibMap.setOnClickListener(FavoritesFragment.this);
		btnAddLoc.setOnClickListener(FavoritesFragment.this);
		
		smfResultLocations.getView().setVisibility(View.INVISIBLE);
		
		return view;
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.ibmap:
				if(lvFavorites.getVisibility() == View.VISIBLE) {
					lvFavorites.setVisibility(View.INVISIBLE);
					smfResultLocations.getView().setVisibility(View.VISIBLE);
				} else if(lvFavorites.getVisibility() == View.INVISIBLE) {
					lvFavorites.setVisibility(View.VISIBLE);
					smfResultLocations.getView().setVisibility(View.INVISIBLE);
				}
				break;
			case R.id.btnaddloc:
				break;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener#onInfoWindowClick(com.google.android.gms.maps.model.Marker)
	 */
	@Override
	public void onInfoWindowClick(Marker marker) {
		JSONObject jObj = markerHashMap.get((Marker) marker);
		
		Intent locDetailsIntent = new Intent(getActivity(), SearchResultDetailsScreen.class);
		locDetailsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION, location);
		locDetailsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, jObj.toString());
		startActivity(locDetailsIntent);
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void getLocations() throws JSONException {
		locations = new MMResultsLocation[favoritesList.length()];
		for(int i = 0; i < favoritesList.length(); i++) {
			JSONObject jObj = favoritesList.getJSONObject(i);
			locations[i] = new MMResultsLocation();
			locations[i].setLocName(jObj.getString(MMAPIConstants.JSON_KEY_NAME));
			locations[i].setLocDist(calcDist(jObj.getDouble(MMAPIConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMAPIConstants.JSON_KEY_LONGITUDE)) + getString(R.string.miles));
			locations[i].setLocAddr(jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS) + MMAPIConstants.DEFAULT_NEWLINE + jObj.getString(MMAPIConstants.JSON_KEY_LOCALITY) + MMAPIConstants.COMMA_SPACE + 
									jObj.getString(MMAPIConstants.JSON_KEY_REGION) + MMAPIConstants.COMMA_SPACE + jObj.getString(MMAPIConstants.JSON_KEY_POSTCODE));
		}
	}
	
	private void addToGoogleMap() throws JSONException {		
		for(int i = 0; i < favoritesList.length(); i++) {
			JSONObject jObj = favoritesList.getJSONObject(i);
			
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
		googleMap.setOnInfoWindowClickListener(FavoritesFragment.this);
		googleMap.setMyLocationEnabled(true);
	}
	
	private String calcDist(double latitude, double longitude) {
		LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		Location favoriteLocation = new Location(location);
		favoriteLocation.setLatitude(latitude);
		favoriteLocation.setLongitude(longitude);
		
		return convertMetersToMiles(location.distanceTo(favoriteLocation));
	}

	private String convertMetersToMiles(double dist) {
		dist = dist * 0.000621371f;
		
		return new DecimalFormat("#.##").format(dist) + MMAPIConstants.DEFAULT_SPACE;
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
            mWindow = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getActivity().getLayoutInflater().inflate(R.layout.custom_info_contents, null);
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
