package com.mobmonkey.mobmonkey.fragments;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobmonkey.mobmonkey.AddLocationScreen;
import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.fragments.FavoritesFragment.OnMMLocationSelectListener;
import com.mobmonkey.mobmonkey.fragments.FavoritesFragment.OnMapIconClickListener;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationListener;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationManager;

/**
 * @author Dezapp, LLC
 *
 */
public class FavoritesMapFragment extends MMFragment implements OnClickListener, OnInfoWindowClickListener {
	private static final String TAG = "MMMapsFragment: ";
	
	private SharedPreferences userPrefs;
	private Location location;
	
	private ImageButton ibMap;
	private Button btnAddLoc;
	private SupportMapFragment smfFavoriteLocations;
	
	private JSONArray favoritesList;
	private GoogleMap googleMap;
	private HashMap<Marker, JSONObject> markerHashMap;
	
	private OnMapIconClickListener mapIconClickListener;
	private OnMMLocationSelectListener locationSelectListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStates) {
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Context.MODE_PRIVATE);
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		View view = inflater.inflate(R.layout.fragment_favorites_map, container, false);
		ibMap = (ImageButton) view.findViewById(R.id.ibmap);
		btnAddLoc = (Button) view.findViewById(R.id.btnaddloc);
		smfFavoriteLocations = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmap);
		
		googleMap = smfFavoriteLocations.getMap();
		markerHashMap = new HashMap<Marker, JSONObject>();		
		
		ibMap.setOnClickListener(FavoritesMapFragment.this);
		btnAddLoc.setOnClickListener(FavoritesMapFragment.this);

		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnMapIconClickListener) {
			mapIconClickListener = (OnMapIconClickListener) activity;
			if(activity instanceof OnMMLocationSelectListener) {
				locationSelectListener = (OnMMLocationSelectListener) activity;
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.ibmap:
				if(MMLocationManager.isGPSEnabled()) {
					mapIconClickListener.onMapIconClicked(MMAPIConstants.FAVORITES_FRAGMENT_LIST);
				}
				break;
			case R.id.btnaddloc:
				if(MMLocationManager.isGPSEnabled()) {
					startActivity(new Intent(getActivity(), AddLocationScreen.class));
				}
				break;
		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		locationSelectListener.onLocationSelect(markerHashMap.get((Marker) marker));
	}

	@Override
	public void onResume() {
		super.onResume();
		if(MMLocationManager.isGPSEnabled()) {
			try {
				refreshFavorites();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, TAG + "onDestroyView");
		try {
			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
			transaction.remove(smfFavoriteLocations);
			transaction.commit();
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public void onFragmentBackPressed() {
		
	}

	/**
	 * Make a call to the server and refresh the Favorites list
	 * @throws JSONException 
	 */
	private void refreshFavorites() throws JSONException {
		String favorites = userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_BOOKMARKS, MMAPIConstants.DEFAULT_STRING);
		if(!favorites.equals(MMAPIConstants.DEFAULT_STRING)) {
			favoritesList = new JSONArray(favorites);
		} else {
			favoritesList = new JSONArray();
		}
		
		addToGoogleMap();
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void addToGoogleMap() throws JSONException {
		markerHashMap.clear();
		googleMap.clear();
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
		googleMap.setOnInfoWindowClickListener(FavoritesMapFragment.this);
		googleMap.setMyLocationEnabled(true);
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
