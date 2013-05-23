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
import android.location.Location;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMSearchResultsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMSearchResultsItem;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMSupportMapFragment;
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
	private FragmentManager fragmentManager;
	
	private JSONArray searchResults;
	private Location location;
	private MMSearchResultsItem[] resultLocations;
	private JSONArray locationHistory;
	
	private TextView tvNavBarTitle;
	private ImageButton ibMap;
	private Button btnAddLoc;
	private Button btnCancel;
	private MMExpandedListView elvSearchResults;
	
	private MMSupportMapFragment smfResultLocations;
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
		Log.d(TAG, TAG + "onCreateView");
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		fragmentManager = getFragmentManager();
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		View view = inflater.inflate(R.layout.fragment_searchresults_screen, container, false);
		tvNavBarTitle = (TextView) view.findViewById(R.id.tvnavbartitle);
		ibMap = (ImageButton) view.findViewById(R.id.ibmap);
		btnAddLoc = (Button) view.findViewById(R.id.btnaddloc);
		btnCancel = (Button) view.findViewById(R.id.btncancel);
		elvSearchResults = (MMExpandedListView) view.findViewById(R.id.elvsearchresults);
		
		tvNavBarTitle.setText(getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE));
		
		try {
			if(!getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS).equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
				searchResults = new JSONArray(getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS));
			} else {
				searchResults = new JSONArray();
			}
			getLocations();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		
		ibMap.setOnClickListener(SearchResultsFragment.this);
		btnAddLoc.setOnClickListener(SearchResultsFragment.this);
		btnCancel.setOnClickListener(SearchResultsFragment.this);
		elvSearchResults.setOnItemClickListener(SearchResultsFragment.this);
		
		ArrayAdapter<MMSearchResultsItem> arrayAdapter = new MMSearchResultsArrayAdapter(getActivity(), R.layout.listview_row_searchresults, resultLocations);
		elvSearchResults.setAdapter(arrayAdapter);
		
		addLocClicked = false;
		
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
			searchResultsLocationSelectListener.onSearchResultsFragmentItemClick(markerHashMap.get((Marker) marker).toString());
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
			MMProgressDialog.displayDialog(getActivity(), MMSDKConstants.DEFAULT_STRING_EMPTY, getString(R.string.pd_retrieving_location_information));
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
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
		try {
			Log.d(TAG, TAG + "onItemClick");
			addToHistory(searchResults.getJSONObject(position));
			
			searchResultsLocationSelectListener.onSearchResultsFragmentItemClick(searchResults.getJSONObject(position).toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		getMMSupportMapFragment();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		try {
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.remove(smfResultLocations);
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
				String address = MMSDKConstants.DEFAULT_STRING_EMPTY;
				address += jObj.isNull(MMSDKConstants.JSON_KEY_ADDRESS) ? MMSDKConstants.DEFAULT_STRING_EMPTY : jObj.getString(MMSDKConstants.JSON_KEY_ADDRESS);
				address += MMSDKConstants.DEFAULT_STRING_NEWLINE;
				
				String localityRegion = MMSDKConstants.DEFAULT_STRING_EMPTY;
				localityRegion += jObj.isNull(MMSDKConstants.JSON_KEY_LOCALITY) ? MMSDKConstants.DEFAULT_STRING_EMPTY : jObj.getString(MMSDKConstants.JSON_KEY_LOCALITY);
				localityRegion += jObj.isNull(MMSDKConstants.JSON_KEY_LOCALITY) || jObj.isNull(MMSDKConstants.JSON_KEY_REGION) ? MMSDKConstants.DEFAULT_STRING_EMPTY : MMSDKConstants.DEFAULT_STRING_COMMA_SPACE;
				localityRegion += jObj.isNull(MMSDKConstants.JSON_KEY_REGION) ? MMSDKConstants.DEFAULT_STRING_EMPTY : jObj.getString(MMSDKConstants.JSON_KEY_REGION);
				
				resultLocations[i].setLocAddr(address + localityRegion);
				Log.d(TAG, i + " stream: " + jObj.getInt(MMSDKConstants.MEDIA_LIVESTREAMING) + " video: " + jObj.getInt(MMSDKConstants.JSON_KEY_VIDEOS) + " images: " + jObj.getInt(MMSDKConstants.JSON_KEY_IMAGES));
			}
	}
	
	/**
	 * @throws JSONException 
	 * 
	 */
	private void getMMSupportMapFragment() {
		smfResultLocations = (MMSupportMapFragment) fragmentManager.findFragmentByTag(MMSDKConstants.MMSUPPORT_MAP_FRAGMENT_TAG);
		if(smfResultLocations == null) {
			smfResultLocations = new MMSupportMapFragment() {
				
				/* (non-Javadoc)
				 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
				 */
				@Override
				public void onActivityCreated(Bundle savedInstanceState) {
					super.onActivityCreated(savedInstanceState);
					googleMap = smfResultLocations.getMap();
					if(googleMap != null) {
						try {
							smfResultLocations.getView().setVisibility(View.INVISIBLE);
							addToGoogleMap();
							getLocationHistory();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			};
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.add(R.id.llsearchresultsmap, smfResultLocations, MMSDKConstants.MMSUPPORT_MAP_FRAGMENT_TAG);
			fragmentTransaction.commit();
		}
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
		markerHashMap = new HashMap<Marker, JSONObject>();
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
		Log.d(TAG, TAG + "my location: " + googleMap.getMyLocation());
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
	 */
	private void ibMapClick() {
		if(elvSearchResults.getVisibility() == View.VISIBLE) {
			displayMap = true;
			elvSearchResults.setVisibility(View.INVISIBLE);
			smfResultLocations.getView().setVisibility(View.VISIBLE);
		} else if(elvSearchResults.getVisibility() == View.INVISIBLE) {
			displayMap = false;
			elvSearchResults.setVisibility(View.VISIBLE);
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
				if(obj instanceof String) {
					if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
						Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
					} else if(((String) obj).equals(MMSDKConstants.SERVICE_NOT_AVAILABLE)) {
						Toast.makeText(getActivity(), R.string.toast_service_not_available, Toast.LENGTH_LONG).show();
					}
				} else if(obj instanceof Address) {
					Address locationClicked = (Address) obj;
					
					Intent intent = new Intent(getActivity(), AddLocationScreen.class);
					intent.putExtra(MMSDKConstants.JSON_KEY_ADDRESS, locationClicked.getAddressLine(MMSDKConstants.DEFAULT_INT_ZERO));
					intent.putExtra(MMSDKConstants.JSON_KEY_LOCALITY, locationClicked.getLocality());
					intent.putExtra(MMSDKConstants.JSON_KEY_REGION, locationClicked.getAdminArea());
					intent.putExtra(MMSDKConstants.JSON_KEY_POSTCODE, locationClicked.getPostalCode());
					intent.putExtra(MMSDKConstants.JSON_KEY_COUNTRY_CODE, locationClicked.getCountryCode());
					intent.putExtra(MMSDKConstants.JSON_KEY_LATITUDE, locationClicked.getLatitude());
					intent.putExtra(MMSDKConstants.JSON_KEY_LONGITUDE, locationClicked.getLongitude());
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
