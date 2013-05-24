package com.mobmonkey.mobmonkeyandroid.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.AddLocationScreen;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMFavoritesArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMSupportMapFragment;
import com.mobmonkey.mobmonkeysdk.adapters.MMFavoritesAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMGeocoderAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;

/**
 * @author Dezapp, LLC
 *
 */
public class FavoritesFragment extends MMFragment implements OnClickListener,
															 OnItemClickListener,
															 OnMapClickListener,
															 OnInfoWindowClickListener {
	private static final String TAG = "FavoritesFragment: ";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	private FragmentManager fragmentManager;
	
	private ImageButton ibMap;
	private Button btnAddLoc;
	private Button btnCancel;
	private ScrollView svFavorites;
	private MMExpandedListView elvFavorites;
	private LinearLayout llFavoritesMap;
	
	private MMFavoritesArrayAdapter favoritesArrayAdapter;
	
	private MMSupportMapFragment smfFavoriteLocations;
	private GoogleMap googleMap;
	
	private HashMap<Marker, JSONObject> markerHashMap;
	private boolean addLocClicked;
	private Marker currMarker;
	private float currZoomLevel = 16;
	private boolean displayMap = false;

	private MMOnSearchResultsFragmentItemClickListener locationSelectListener;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, TAG + "onCreateView");
		
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		fragmentManager = getFragmentManager();
		
		View view = inflater.inflate(R.layout.fragment_favorites_screen, container, false);
		ibMap = (ImageButton) view.findViewById(R.id.ibmap);
		btnAddLoc = (Button) view.findViewById(R.id.btnaddloc);
		btnCancel = (Button) view.findViewById(R.id.btncancel);
		svFavorites = (ScrollView) view.findViewById(R.id.svfavorites);
		elvFavorites = (MMExpandedListView) view.findViewById(R.id.elvfavorites);
		llFavoritesMap = (LinearLayout) view.findViewById(R.id.llfavoritesmap);

		markerHashMap = new HashMap<Marker, JSONObject>();		
		addLocClicked = false;
		
		ibMap.setOnClickListener(FavoritesFragment.this);
		btnAddLoc.setOnClickListener(FavoritesFragment.this);
		btnCancel.setOnClickListener(FavoritesFragment.this);
		elvFavorites.setOnItemClickListener(FavoritesFragment.this);
		
		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MMOnSearchResultsFragmentItemClickListener) {
			locationSelectListener = (MMOnSearchResultsFragmentItemClickListener) activity;
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == MMSDKConstants.REQUEST_CODE_ADD_LOCATION) {
			if(resultCode == Activity.RESULT_OK) {
				Log.d(TAG, TAG + "info: " + data.getStringExtra(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));
				locationSelectListener.onSearchResultsFragmentItemClick(data.getStringExtra(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
			switch(view.getId()) {
				case R.id.ibmap:
					mapButtonClicked();
					break;
				case R.id.btnaddloc:
					addLocButtonClicked();
					break;
				case R.id.btncancel:
					cancelButtonClicked();
					break;
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		locationSelectListener.onSearchResultsFragmentItemClick(favoritesArrayAdapter.getItem(position).toString());
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.android.gms.maps.GoogleMap.OnMapClickListener#onMapClick(com.google.android.gms.maps.model.LatLng)
	 */
	@Override
	public void onMapClick(LatLng pointClicked) {
		Log.d(TAG, TAG + "addLocClicked: " + addLocClicked);
		if(addLocClicked) {
			MMGeocoderAdapter.getFromLocation(getActivity(),
											  new ReverseGeocodeCallback(),
											  pointClicked.latitude,
											  pointClicked.longitude);
			MMProgressDialog.displayDialog(getActivity(),
										   MMSDKConstants.DEFAULT_STRING_EMPTY,
										   getString(R.string.pd_retrieving_location_information));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener#onInfoWindowClick(com.google.android.gms.maps.model.Marker)
	 */
	@Override
	public void onInfoWindowClick(Marker marker) {
		Log.d(TAG, TAG + "marker clicked: " + marker);
		currMarker = marker;
		currZoomLevel = googleMap.getCameraPosition().zoom;
		locationSelectListener.onSearchResultsFragmentItemClick(markerHashMap.get((Marker) marker).toString());
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.d(TAG, TAG + "onResume");
		super.onResume();
		if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
			refreshFavorites();
			getMMSupportMapFragment();
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		Log.d(TAG, TAG + "onPause");
		super.onPause();
		try {
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.remove(smfFavoriteLocations);
			transaction.commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		
	}
	
	/**
	 * 
	 */
	private void mapButtonClicked() {
		if(!displayMap) {
			svFavorites.setVisibility(View.GONE);
			llFavoritesMap.setVisibility(View.VISIBLE);
			displayMap = true;
		} else {
			svFavorites.setVisibility(View.VISIBLE);
			llFavoritesMap.setVisibility(View.GONE);
			displayMap = false;
		}
	}
	
	/**
	 * 
	 */
	private void addLocButtonClicked() {
		Log.d(TAG, TAG + "displayMap: " + displayMap);
		if(!displayMap) {
			Intent intent = new Intent(getActivity(), AddLocationScreen.class);
			intent.putExtra(MMSDKConstants.REQUEST_CODE, MMSDKConstants.REQUEST_CODE_ADD_LOCATION);
			startActivityForResult(intent, MMSDKConstants.REQUEST_CODE_ADD_LOCATION);
		} else {
			Toast.makeText(getActivity(), R.string.toast_tap_location_to_add, Toast.LENGTH_LONG).show();
			addLocClicked = true;
			btnAddLoc.setVisibility(View.INVISIBLE);
			btnCancel.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ibMap.getLayoutParams();
			params.addRule(RelativeLayout.LEFT_OF, R.id.btncancel);
			ibMap.setLayoutParams(params);
		}
	}
	
	/**
	 * 
	 */
	private void cancelButtonClicked() {
		addLocClicked = false;
		btnAddLoc.setVisibility(View.VISIBLE);
		btnCancel.setVisibility(View.INVISIBLE);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ibMap.getLayoutParams();
		params.addRule(RelativeLayout.LEFT_OF, R.id.btnaddloc);
		ibMap.setLayoutParams(params);
	}
	
	/**
	 * Make a call to the server and refresh the Favorites list
	 */
	private void refreshFavorites() {
		MMFavoritesAdapter.getFavorites(new FavoritesCallback(),
										MMConstants.PARTNER_ID,
										userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
										userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
		MMProgressDialog.displayDialog(getActivity(),
									   MMSDKConstants.DEFAULT_STRING_EMPTY,
									   getString(R.string.pd_updating_favorites));
	}
	
	/**
	 * @throws JSONException 
	 * 
	 */
	private void getMMSupportMapFragment() {
		smfFavoriteLocations = (MMSupportMapFragment) fragmentManager.findFragmentByTag(MMSDKConstants.MMSUPPORT_MAP_FRAGMENT_TAG);
		if(smfFavoriteLocations == null) {
			smfFavoriteLocations = new MMSupportMapFragment() {
				/* (non-Javadoc)
				 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
				 */
				@Override
				public void onActivityCreated(Bundle savedInstanceState) {
					super.onActivityCreated(savedInstanceState);
					googleMap = smfFavoriteLocations.getMap();
				}
			};
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.add(R.id.llfavoritesmap, smfFavoriteLocations, MMSDKConstants.MMSUPPORT_MAP_FRAGMENT_TAG);
			fragmentTransaction.commit();
		}
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void getFavorites(String result) throws JSONException {
		JSONArray favorites = new JSONArray(result);
		userPrefsEditor.putString(MMSDKConstants.SHARED_PREFS_KEY_FAVORITES, result);
		userPrefsEditor.apply();
		markerHashMap.clear();
		googleMap.clear();
		
		if(favorites.length() > 0) {
			ArrayList<JSONObject> favoriteLocations = new ArrayList<JSONObject>();
			
			for(int i = 0; i < favorites.length(); i++) {
				favoriteLocations.add(favorites.getJSONObject(i));
			}

			favoritesArrayAdapter = new MMFavoritesArrayAdapter(getActivity(), R.layout.listview_row_favorites, favoriteLocations);
			elvFavorites.setAdapter(favoritesArrayAdapter);

			addToGoogleMap(favorites);
		}
		
		if(displayMap) {
			svFavorites.setVisibility(View.GONE);
			llFavoritesMap.setVisibility(View.VISIBLE);
		} else {
			svFavorites.setVisibility(View.VISIBLE);
			elvFavorites.setVisibility(View.VISIBLE);
			llFavoritesMap.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void addToGoogleMap(JSONArray favorites) throws JSONException {
		LatLng currentLoc = new LatLng(MMLocationManager.getLocationLatitude(), MMLocationManager.getLocationLongitude());
		googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		
		for(int i = 0; i < favorites.length(); i++) {
			JSONObject jObj = favorites.getJSONObject(i);
			
			LatLng resultLocLatLng = new LatLng(jObj.getDouble(MMSDKConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE));
			
			Marker locationResultMarker = googleMap.addMarker(new MarkerOptions().
					position(resultLocLatLng).
					title(jObj.getString(MMSDKConstants.JSON_KEY_NAME))
					.snippet(jObj.getString(MMSDKConstants.JSON_KEY_ADDRESS)));
			
			if(currMarker != null && currMarker.getTitle().equals(locationResultMarker.getTitle()) && currMarker.getTitle().equals(locationResultMarker.getTitle())) {
				Log.d(TAG, TAG + "marker equal");
				locationResultMarker.showInfoWindow();
				currentLoc = currMarker.getPosition();
			}
			
			markerHashMap.put(locationResultMarker, jObj);
		}

		Log.d(TAG, TAG + "lat: " + currentLoc.latitude + " long:  " + currentLoc.longitude);
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, currZoomLevel));
		
		googleMap.setOnInfoWindowClickListener(FavoritesFragment.this);
		googleMap.setOnMapClickListener(FavoritesFragment.this);
		googleMap.setMyLocationEnabled(true);
		Log.d(TAG, TAG + "my location: " + googleMap.getMyLocation());
	}
	
	/**
	 * Callback to update the user's favorites list in app data after making get favorites call to the server
	 * @author Dezapp, LLC
	 *
	 */
	private class FavoritesCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					Log.d(TAG, TAG + "response: " + ((String) obj));
					try {
						getFavorites((String) obj);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class ReverseGeocodeCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				if(obj instanceof String) {
					if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
						Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
					} else if(((String) obj).equals(MMSDKConstants.SERVICE_NOT_AVAILABLE)) {
						Toast.makeText(getActivity(), R.string.toast_service_not_available, Toast.LENGTH_LONG).show();
					}
				} else if(obj instanceof Address){
					Address locationClicked = (Address) obj;
					
					Intent intent = new Intent(getActivity(), AddLocationScreen.class);
					intent.putExtra(MMSDKConstants.JSON_KEY_ADDRESS, locationClicked.getAddressLine(MMSDKConstants.DEFAULT_INT_ZERO));
					intent.putExtra(MMSDKConstants.JSON_KEY_LOCALITY, locationClicked.getLocality());
					intent.putExtra(MMSDKConstants.JSON_KEY_REGION, locationClicked.getAdminArea());
					intent.putExtra(MMSDKConstants.JSON_KEY_POSTCODE, locationClicked.getPostalCode());
					intent.putExtra(MMSDKConstants.JSON_KEY_COUNTRY_CODE, locationClicked.getCountryCode());
					intent.putExtra(MMSDKConstants.JSON_KEY_LATITUDE, locationClicked.getLatitude());
					intent.putExtra(MMSDKConstants.JSON_KEY_LONGITUDE, locationClicked.getLongitude());
					startActivityForResult(intent, MMSDKConstants.REQUEST_CODE_ADD_LOCATION);
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
               titleUi.setText(MMSDKConstants.DEFAULT_STRING_EMPTY);
           }

           String snippet = marker.getSnippet();
           TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
           if (snippet != null) {
               snippetUi.setText(snippet);
           } else {
               snippetUi.setText(MMSDKConstants.DEFAULT_STRING_EMPTY);
           }
       }
   }
}
