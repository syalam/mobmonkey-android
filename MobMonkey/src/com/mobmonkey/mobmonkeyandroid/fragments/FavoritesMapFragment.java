package com.mobmonkey.mobmonkeyandroid.fragments;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMSupportMapFragment;
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
public class FavoritesMapFragment extends MMFragment implements OnClickListener, 
																OnInfoWindowClickListener, 
																OnMapClickListener {
	private static final String TAG = "MMMapsFragment: ";
	
	private SharedPreferences userPrefs;
	private FragmentManager fragmentManager;
	
	private ImageButton ibMap;
	private Button btnAddLoc;
	private Button btnCancel;
	
	private MMSupportMapFragment smfFavoriteLocations;
	private GoogleMap googleMap;
	
	private JSONArray favoritesList;
	private HashMap<Marker, JSONObject> markerHashMap;
	private boolean addLocClicked;
	private Marker currMarker;
	private float currZoomLevel = 17;
	
	private MMOnMapIconFragmentClickListener mapIconClickListener;
	private MMOnSearchResultsFragmentItemClickListener locationSelectListener;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStates) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		fragmentManager = getFragmentManager();
		
		View view = inflater.inflate(R.layout.fragment_favorites_map, container, false);
		ibMap = (ImageButton) view.findViewById(R.id.ibmap);
		btnAddLoc = (Button) view.findViewById(R.id.btnaddloc);
		btnCancel = (Button) view.findViewById(R.id.btncancel);

		markerHashMap = new HashMap<Marker, JSONObject>();		
		addLocClicked = false;
		
		ibMap.setOnClickListener(FavoritesMapFragment.this);
		btnAddLoc.setOnClickListener(FavoritesMapFragment.this);
		btnCancel.setOnClickListener(FavoritesMapFragment.this);
		
		return view;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MMOnMapIconFragmentClickListener) {
			mapIconClickListener = (MMOnMapIconFragmentClickListener) activity;
			if(activity instanceof MMOnSearchResultsFragmentItemClickListener) {
				locationSelectListener = (MMOnSearchResultsFragmentItemClickListener) activity;
			}
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
				cancelButtonClicked();
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
		switch(view.getId()) {
			case R.id.ibmap:
				if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
					mapIconClickListener.onMapIconFragmentClick(MMSDKConstants.FAVORITES_FRAGMENT_LIST);
				}
				break;
			case R.id.btnaddloc:
				if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
					Toast.makeText(getActivity(), R.string.toast_tap_location_to_add, Toast.LENGTH_LONG).show();
					addLocClicked = true;
					btnAddLoc.setVisibility(View.INVISIBLE);
					btnCancel.setVisibility(View.VISIBLE);
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ibMap.getLayoutParams();
					params.addRule(RelativeLayout.LEFT_OF, R.id.btncancel);
					ibMap.setLayoutParams(params);
				}
				break;
			case R.id.btncancel:
				cancelButtonClicked();
				break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener#onInfoWindowClick(com.google.android.gms.maps.model.Marker)
	 */
	@Override
	public void onInfoWindowClick(Marker marker) {
		currMarker = marker;
		currZoomLevel = googleMap.getCameraPosition().zoom;
		locationSelectListener.onSearchResultsFragmentItemClick(markerHashMap.get((Marker) marker).toString());
	}

	@Override
	public void onMapClick(LatLng pointClicked) {
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
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
			try {
				refreshFavorites();
				getMMSupportMapFragment();
			} catch (JSONException e) {
				e.printStackTrace();
			}
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

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, TAG + "onDestroyView");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		
	}

	/**
	 * Make a call to the server and refresh the Favorites list
	 * @throws JSONException 
	 */
	private void refreshFavorites() throws JSONException {
		String favorites = userPrefs.getString(MMSDKConstants.SHARED_PREFS_KEY_FAVORITES, MMSDKConstants.DEFAULT_STRING_EMPTY);
		if(!favorites.equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
			favoritesList = new JSONArray(favorites);
		} else {
			favoritesList = new JSONArray();
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
					if(googleMap != null) {
						try {
							addToGoogleMap();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
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
	private void addToGoogleMap() throws JSONException {
		markerHashMap.clear();
		googleMap.clear();
		LatLng currentLoc = new LatLng(MMLocationManager.getLocationLatitude(), MMLocationManager.getLocationLongitude());
		googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		
		for(int i = 0; i < favoritesList.length(); i++) {
			JSONObject jObj = favoritesList.getJSONObject(i);
			
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
		googleMap.setOnInfoWindowClickListener(FavoritesMapFragment.this);
		googleMap.setOnMapClickListener(FavoritesMapFragment.this);
		googleMap.setMyLocationEnabled(true);
		Log.d(TAG, TAG + "my location: " + googleMap.getMyLocation());
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
