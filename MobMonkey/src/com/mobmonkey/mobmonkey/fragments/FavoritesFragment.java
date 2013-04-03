package com.mobmonkey.mobmonkey.fragments;

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
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.internal.ac;
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
import com.mobmonkey.mobmonkey.SearchResultDetailsScreen;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkey.utils.MMResultsLocation;
import com.mobmonkey.mobmonkey.utils.MMSearchResultsArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMUtility;
import com.mobmonkey.mobmonkeyapi.adapters.MMBookmarksAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationListener;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationManager;

/**
 * @author Dezapp, LLC
 *
 */
public class FavoritesFragment extends MMFragment implements OnClickListener, OnItemClickListener {//, OnInfoWindowClickListener {
	private static final String TAG = "FavoritesFragment: ";
	
	private SharedPreferences userPrefs;
//	private SharedPreferences.Editor userPrefsEditor;
	private Location location;
	
	private ImageButton ibMap;
	private Button btnAddLoc;
	private ListView lvFavorites;
	
	private MMResultsLocation[] favoriteLocations;
	private JSONArray favoritesList;
	
	private OnMapIconClickListener mapIconClickListener;
	private OnMMLocationSelectListener locationSelectListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, TAG + "onCreateView");
		
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Context.MODE_PRIVATE);
//		userPrefsEditor = userPrefs.edit();
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		View view = inflater.inflate(R.layout.fragment_favorites_screen, container, false);
		ibMap = (ImageButton) view.findViewById(R.id.ibmap);
		btnAddLoc = (Button) view.findViewById(R.id.btnaddloc);
		lvFavorites = (ListView) view.findViewById(R.id.lvbookmarks);

		ibMap.setOnClickListener(FavoritesFragment.this);
		btnAddLoc.setOnClickListener(FavoritesFragment.this);
		lvFavorites.setOnItemClickListener(FavoritesFragment.this);
		
//		if(userPrefs.getInt(MMAPIConstants.KEY_INTENT_EXTRA_DISPLAY_MAP, View.GONE) == View.VISIBLE) {
//			smfFavoriteLocations.getView().setVisibility(userPrefs.getInt(MMAPIConstants.KEY_INTENT_EXTRA_DISPLAY_MAP, View.GONE));
//			lvFavorites.setVisibility(View.INVISIBLE);
//		} else {
//			smfFavoriteLocations.getView().setVisibility(View.GONE);
//		}
		
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
					mapIconClickListener.onMapIconClicked(MMAPIConstants.FAVORITES_FRAGMENT_MAP);
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
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		try {
			locationSelectListener.onLocationSelect(favoritesList.getJSONObject(position));
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
		Log.d(TAG, TAG + "onDestroyView");
//		Log.d(TAG, TAG + "visibility: " + smfFavoriteLocations.getView().getVisibility());
//		userPrefsEditor.putInt(MMAPIConstants.KEY_INTENT_EXTRA_DISPLAY_MAP, smfFavoriteLocations.getView().getVisibility());
//		userPrefsEditor.commit();
//		try {
//			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//			transaction.remove(smfFavoriteLocations);
//			transaction.commit();
//		} catch (Exception e) {
//			
//		}

		super.onDestroyView();
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, TAG + "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		Log.d(TAG, TAG + "onDetach");
		super.onDetach();
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		
	}
	
//	/* (non-Javadoc)
//	 * @see com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener#onInfoWindowClick(com.google.android.gms.maps.model.Marker)
//	 */
//	@Override
//	public void onInfoWindowClick(Marker marker) {
//		locationSelectedListener.onLocationSelected(markerHashMap.get((Marker) marker));
//	}
	
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
		getFavorites();
		ArrayAdapter<MMResultsLocation> arrayAdapter = new MMSearchResultsArrayAdapter(getActivity(), R.layout.search_result_list_row, favoriteLocations);
		lvFavorites.setAdapter(arrayAdapter);
//		addToGoogleMap();
		
//		MMBookmarksAdapter.getBookmarks(new FavoritesCallback(), 
//										MMAPIConstants.URL_BOOKMARKS, 
//										MMConstants.PARTNER_ID, 
//										userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
//										userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void getFavorites() throws JSONException {
		favoriteLocations = new MMResultsLocation[favoritesList.length()];
		for(int i = 0; i < favoritesList.length(); i++) {
			JSONObject jObj = favoritesList.getJSONObject(i);
			favoriteLocations[i] = new MMResultsLocation();
			favoriteLocations[i].setLocName(jObj.getString(MMAPIConstants.JSON_KEY_NAME));
			favoriteLocations[i].setLocDist(MMUtility.calcDist(location, jObj.getDouble(MMAPIConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMAPIConstants.JSON_KEY_LONGITUDE)) + getString(R.string.miles));
			favoriteLocations[i].setLocAddr(jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS) + MMAPIConstants.DEFAULT_NEWLINE + jObj.getString(MMAPIConstants.JSON_KEY_LOCALITY) + MMAPIConstants.COMMA_SPACE + 
									jObj.getString(MMAPIConstants.JSON_KEY_REGION) + MMAPIConstants.COMMA_SPACE + jObj.getString(MMAPIConstants.JSON_KEY_POSTCODE));
		}
		
		// reverse array
		List temp = Arrays.asList(favoriteLocations);
		Collections.reverse(temp);
		favoriteLocations = (MMResultsLocation[]) temp.toArray();
		
		temp = new ArrayList<JSONObject>();
		for(int i = 0; i < favoritesList.length(); i++) {
			temp.add(favoritesList.get(i));
		}
		Collections.reverse(temp);
		favoritesList = new JSONArray(temp);
	}
	
//	/**
//	 * 
//	 * @throws JSONException
//	 */
//	private void addToGoogleMap() throws JSONException {
//		markerHashMap.clear();
//		googleMap.clear();
//		for(int i = 0; i < favoritesList.length(); i++) {
//			JSONObject jObj = favoritesList.getJSONObject(i);
//			
//			LatLng resultLocLatLng = new LatLng(jObj.getDouble(MMAPIConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMAPIConstants.JSON_KEY_LONGITUDE));
//			
//			Marker locationResultMarker = googleMap.addMarker(new MarkerOptions().
//					position(resultLocLatLng).
//					title(jObj.getString(MMAPIConstants.JSON_KEY_NAME))
//					.snippet(jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS)));
//			
//			markerHashMap.put(locationResultMarker, jObj);
//		}
//		
//		LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
//		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 16));
//		googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
//		googleMap.setOnInfoWindowClickListener(FavoritesFragment.this);
//		googleMap.setMyLocationEnabled(true);
//	}
	
	public interface OnMapIconClickListener {
		public void onMapIconClicked(int which);
	}
	
	public interface OnMMLocationSelectListener {
		public void onLocationSelect(Object obj);
	}
	
//	private class FavoritesCallback implements MMCallback {
//		@Override
//		public void processCallback(Object obj) {
//			if(obj != null) {
//				try {
//					Log.d(TAG, TAG + "response: " + ((String) obj));
//					favoritesList = new JSONArray((String) obj);
//					if(MMLocationManager.isGPSEnabled()) {
//						getFavorites();
//						ArrayAdapter<MMResultsLocation> arrayAdapter = new MMSearchResultsArrayAdapter(getActivity(), R.layout.search_result_list_row, favoriteLocations);
//						lvFavorites.setAdapter(arrayAdapter);
//						addToGoogleMap();
//					}
//				} catch (JSONException e) {
//					
//					e.printStackTrace();
//				}
//			}
//		}
//	}
	
//	/**
//	 * 
//	 * @author Dezapp, LLC
//	 *
//	 */
//	private class CustomInfoWindowAdapter implements InfoWindowAdapter {
//        private final View mWindow;
//        private final View mContents;
//
//        public CustomInfoWindowAdapter() {
//            mWindow = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
//            mContents = getActivity().getLayoutInflater().inflate(R.layout.custom_info_contents, null);
//        }
//
//        @Override
//        public View getInfoWindow(Marker marker) {
//            render(marker, mWindow);
//            return mWindow;
//        }
//
//        @Override
//        public View getInfoContents(Marker marker) {
//            render(marker, mContents);
//            return mContents;
//        }
//
//        private void render(Marker marker, View view) {
//            String title = marker.getTitle();
//            TextView titleUi = ((TextView) view.findViewById(R.id.title));
//            if (title != null) {
//                titleUi.setText(title);
//            } else {
//                titleUi.setText(MMAPIConstants.DEFAULT_STRING);
//            }
//
//            String snippet = marker.getSnippet();
//            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
//            if (snippet != null) {
//                snippetUi.setText(snippet);
//            } else {
//                snippetUi.setText(MMAPIConstants.DEFAULT_STRING);
//            }
//        }
//    }
}
