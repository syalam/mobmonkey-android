package com.mobmonkey.mobmonkeyandroid.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.AddLocationScreen;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMSearchResultsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMSearchResultsItem;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
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
public class SearchResultsFragment extends MMFragment implements OnClickListener, 
																 OnItemClickListener, 
																 OnInfoWindowClickListener,
																 OnMapClickListener {
	private static final String TAG = "SearchResultsScreen: ";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	
	private JSONArray searchResults;
	private Location location;
	private MMSearchResultsItem[] resultLocations;
	private JSONArray locationHistory;
	
	private TextView tvNavBarTitle;
	private ImageButton ibMap;
	private Button btnAddLoc;
	private Button btnCancel;
	private ListView lvSearchResults;
	private SupportMapFragment smfResultLocations;
	private GoogleMap googleMap;
	private boolean displayMap = false;
	private boolean addLocClicked;
	private Marker currMarker;
	private float currZoomLevel = 16;
	
	private HashMap<Marker, JSONObject> markerHashMap;
	private MMOnSearchResultsFragmentItemClickListener searchResultsLocationSelectListener;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		View view = inflater.inflate(R.layout.fragment_searchresults_screen, container, false);
		tvNavBarTitle = (TextView) view.findViewById(R.id.tvnavbartitle);
		ibMap = (ImageButton) view.findViewById(R.id.ibmap);
		btnAddLoc = (Button) view.findViewById(R.id.btnaddloc);
		btnCancel = (Button) view.findViewById(R.id.btncancel);
		smfResultLocations = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmap);
		lvSearchResults = (ListView) view.findViewById(R.id.lvsearchresults);
		
		tvNavBarTitle.setText(getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE));
		
		try {
			if(!getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS).equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
				searchResults = new JSONArray(getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS));
			} else {
				searchResults = new JSONArray();
			}
			getLocations();
			displayMap();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		
		ibMap.setOnClickListener(SearchResultsFragment.this);
		btnAddLoc.setOnClickListener(SearchResultsFragment.this);
		btnCancel.setOnClickListener(SearchResultsFragment.this);
		lvSearchResults.setOnItemClickListener(SearchResultsFragment.this);
		
		ArrayAdapter<MMSearchResultsItem> arrayAdapter = new MMSearchResultsArrayAdapter(getActivity(), R.layout.listview_row_searchresults, resultLocations);
		lvSearchResults.setAdapter(arrayAdapter);
		
		addLocClicked = false;
		
		if(displayMap) {
			lvSearchResults.setVisibility(View.INVISIBLE);
			smfResultLocations.getView().setVisibility(View.VISIBLE);
		} else {
			lvSearchResults.setVisibility(View.VISIBLE);
			smfResultLocations.getView().setVisibility(View.INVISIBLE);
		}
		
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
			searchResultsLocationSelectListener = (MMOnSearchResultsFragmentItemClickListener) activity;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener#onInfoWindowClick(com.google.android.gms.maps.model.Marker)
	 */
	@Override
	public void onInfoWindowClick(Marker marker) {
		try {
			addToHistory(markerHashMap.get((Marker) marker));
			currMarker = marker;
			currZoomLevel = googleMap.getCameraPosition().zoom;
			searchResultsLocationSelectListener.onSearchResultsFragmentItemClick(markerHashMap.get((Marker) marker));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.android.gms.maps.GoogleMap.OnMapClickListener#onMapClick(com.google.android.gms.maps.model.LatLng)
	 */
	@Override
	public void onMapClick(LatLng pointClicked) {
		if(addLocClicked) {
			MMGeocoderAdapter.getFromLocation(getActivity(), new ReverseGeocodeCallback(), pointClicked.latitude, pointClicked.longitude);
			MMProgressDialog.displayDialog(getActivity(), MMSDKConstants.DEFAULT_STRING_EMPTY, "");
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
				ibMapClick();
				break;
			case R.id.btnaddloc:
				buttonAddLocClick();
				break;
			case R.id.btncancel:
				buttonCancelClick();
				break;
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
			addToHistory(searchResults.getJSONObject(position));
			
			try {
				searchResultsLocationSelectListener.onSearchResultsFragmentItemClick(searchResults.getJSONObject(position));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		try {
			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
			transaction.remove(smfResultLocations);
			transaction.commit();
		} catch (Exception e) {
			
		}
		super.onDestroyView();
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {

	}

	/**
	 * 
	 * @throws JSONException
	 */
	private void getLocations() throws JSONException {
		resultLocations = new MMSearchResultsItem[searchResults.length()];
			for(int i = 0; i < searchResults.length(); i++) {
				JSONObject jObj = searchResults.getJSONObject(i);
				resultLocations[i] = new MMSearchResultsItem();
				resultLocations[i].setLocName(jObj.getString(MMSDKConstants.JSON_KEY_NAME));
				resultLocations[i].setLocDist(MMUtility.calcDist(location, jObj.getDouble(MMSDKConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE)) + MMSDKConstants.DEFAULT_STRING_SPACE + 
						getString(R.string.miles));
				resultLocations[i].setLocAddr(jObj.getString(MMSDKConstants.JSON_KEY_ADDRESS) + MMSDKConstants.DEFAULT_STRING_NEWLINE + jObj.getString(MMSDKConstants.JSON_KEY_LOCALITY) + MMSDKConstants.DEFAULT_STRING_COMMA_SPACE + 
										jObj.getString(MMSDKConstants.JSON_KEY_REGION) + MMSDKConstants.DEFAULT_STRING_COMMA_SPACE + jObj.getString(MMSDKConstants.JSON_KEY_POSTCODE));
				Log.d(TAG, i + " stream: " + jObj.getInt(MMSDKConstants.MEDIA_LIVESTREAMING) + " video: " + jObj.getInt(MMSDKConstants.JSON_KEY_VIDEOS) + " images: " + jObj.getInt(MMSDKConstants.JSON_KEY_IMAGES));
			}
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void displayMap() throws JSONException {
		googleMap = smfResultLocations.getMap();
		markerHashMap = new HashMap<Marker, JSONObject>();
		addToGoogleMap();
		getLocationHistory();
	}
	
	/**
	 * 
	 * @return
	 * @throws JSONException
	 */
	private boolean getLocationHistory() throws JSONException {
		String history = userPrefs.getString(MMSDKConstants.SHARED_PREFS_KEY_HISTORY, MMSDKConstants.DEFAULT_STRING_EMPTY);
		if(!history.equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
			locationHistory = new JSONArray(history);
			return true;
		} else {
			locationHistory = new JSONArray();
			return false;
		}
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void addToGoogleMap() throws JSONException {
		LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
		googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		
		for(int i = 0; i < searchResults.length(); i++) {
			JSONObject jObj = searchResults.getJSONObject(i);
			
			LatLng resultLocLatLng = new LatLng(jObj.getDouble(MMSDKConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE));
			
			Marker locationResultMarker = googleMap.addMarker(new MarkerOptions().
					position(resultLocLatLng).
					title(jObj.getString(MMSDKConstants.JSON_KEY_NAME))
					.snippet(jObj.getString(MMSDKConstants.JSON_KEY_ADDRESS)));
			
			if(currMarker != null && currMarker.getTitle().equals(locationResultMarker.getTitle())) {
				Log.d(TAG, TAG + "marker equal");
				locationResultMarker.showInfoWindow();
				currentLoc = currMarker.getPosition();
			}
			
			markerHashMap.put(locationResultMarker, jObj);
		}
		
		if(currMarker != null) {
			currentLoc = currMarker.getPosition();
		}
		
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, currZoomLevel));
		googleMap.setOnInfoWindowClickListener(SearchResultsFragment.this);
		googleMap.setOnMapClickListener(SearchResultsFragment.this);
		googleMap.setMyLocationEnabled(true);
	}
	
	/**
	 * 
	 * @param position
	 * @throws JSONException
	 */
	private void addToHistory(JSONObject loc) throws JSONException {
		if(!locationExistsInHistory(loc)) {
			if(locationHistory.length() < MMSDKConstants.HISTORY_SIZE) {
				ArrayList<JSONObject> temp = new ArrayList<JSONObject>();
				//Convert to ArrayList so that you can add last item view to front of array
				for (int i=0; i<locationHistory.length(); i++)
					temp.add(locationHistory.getJSONObject(i));
				temp.add(0, loc);
				locationHistory = new JSONArray(temp);
			} else {
				ArrayList<JSONObject> temp = new ArrayList<JSONObject>();
				//Convert to ArrayList so that you can add last item view to front of array
				for (int i=0; i<locationHistory.length(); i++)
					temp.add(locationHistory.getJSONObject(i));
				temp.add(0, loc);
				temp.remove(MMSDKConstants.HISTORY_SIZE);
				locationHistory = new JSONArray(temp);
			}
		}
		userPrefsEditor.putString(MMSDKConstants.SHARED_PREFS_KEY_HISTORY, locationHistory.toString());
		userPrefsEditor.commit();
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
			if(locationHistory.getJSONObject(i).getString(MMSDKConstants.JSON_KEY_NAME).equals(loc.getString(MMSDKConstants.JSON_KEY_NAME)) &&
					locationHistory.getJSONObject(i).getString(MMSDKConstants.JSON_KEY_LATITUDE).equals(loc.getString(MMSDKConstants.JSON_KEY_LATITUDE)) &&
					locationHistory.getJSONObject(i).getString(MMSDKConstants.JSON_KEY_LONGITUDE).equals(loc.getString(MMSDKConstants.JSON_KEY_LONGITUDE))) {
				ArrayList<JSONObject> temp = new ArrayList<JSONObject>();
				//Convert to ArrayList so that you can add last item view to front of array
				for (int j=0; j<locationHistory.length(); j++)
					temp.add(locationHistory.getJSONObject(j));
				JSONObject tempObj = temp.remove(i);
				temp.add(0, tempObj);
				locationHistory = new JSONArray(temp);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 * @throws IOException
	 */
	public Address getAddressForLocation(double latitude, double longitude) throws IOException {
        int maxResults = 1;

        Geocoder gc = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = gc.getFromLocation(latitude, longitude, maxResults);

        if (addresses.size() == 1) {
            return addresses.get(0);
        } else {
            return null;
        }
    }
	
	/**
	 * 
	 */
	private void ibMapClick() {
		if(lvSearchResults.getVisibility() == View.VISIBLE) {
			displayMap = true;
			lvSearchResults.setVisibility(View.INVISIBLE);
			smfResultLocations.getView().setVisibility(View.VISIBLE);
		} else if(lvSearchResults.getVisibility() == View.INVISIBLE) {
			displayMap = false;
			lvSearchResults.setVisibility(View.VISIBLE);
			smfResultLocations.getView().setVisibility(View.INVISIBLE);
			btnAddLoc.setVisibility(View.VISIBLE);
			btnCancel.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 
	 */
	private void buttonAddLocClick() {
		if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
			if(smfResultLocations.getView().getVisibility() == View.INVISIBLE) {
				startActivity(new Intent(getActivity(), AddLocationScreen.class));
			} else {
				Toast.makeText(getActivity(), R.string.toast_tap_location_to_add, Toast.LENGTH_LONG).show();
				addLocClicked = true;
				btnAddLoc.setVisibility(View.GONE);
				btnCancel.setVisibility(View.VISIBLE);
			}
		}
	}
	
	/**
	 * 
	 */
	private void buttonCancelClick() {
		addLocClicked = false;
		btnAddLoc.setVisibility(View.VISIBLE);
		btnCancel.setVisibility(View.GONE);
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
				
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					Address locationClicked = (Address) obj;
				
					Bundle bundle = new Bundle();
					bundle.putString(MMSDKConstants.JSON_KEY_ADDRESS, locationClicked.getAddressLine(0));
					bundle.putString(MMSDKConstants.JSON_KEY_LOCALITY, locationClicked.getLocality());
					bundle.putString(MMSDKConstants.JSON_KEY_REGION, locationClicked.getAdminArea());
					bundle.putString(MMSDKConstants.JSON_KEY_POSTCODE, locationClicked.getPostalCode());
					bundle.putDouble(MMSDKConstants.JSON_KEY_LATITUDE, locationClicked.getLatitude());
					bundle.putDouble(MMSDKConstants.JSON_KEY_LONGITUDE, locationClicked.getLongitude());
					
					Intent intent = new Intent(getActivity(), AddLocationScreen.class);
					intent.putExtras(bundle);
					startActivity(intent);
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
