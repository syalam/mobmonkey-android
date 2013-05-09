package com.mobmonkey.mobmonkeyandroid.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMSearchCategoriesArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMSearchResultsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMSearchCategoriesItem;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMSearchResultsItem;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnCategoryFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnHistoryFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnNearbyLocationsItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnSearchTextFragmentCompleteListener;
import com.mobmonkey.mobmonkeyandroid.utils.MMCategories;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedNearbyLocationsListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMScrollView;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMSearchLocationAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchLocationsFragment extends MMFragment implements OnClickListener,
																   OnEditorActionListener,
																   OnInfoWindowClickListener,
																   OnMapLongClickListener {
	private static final String TAG = "SearchLocationsFragment: ";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	
	private Location location;
	private JSONArray nearbyLocations;
	private JSONArray locationHistory;
	
	private LinearLayout llCreateHotSpot;
	private EditText etSearch;
	private MMScrollView svNearbyLocations;
	private MMExpandedNearbyLocationsListView elvNearbyLocations;
	private LinearLayout llLoadMore;
	private TextView tvHoldToPanAndZoom;
	private SupportMapFragment smfNearbyLocations;
	private MMExpandedListView elvSearch;
	
	private GoogleMap googleMap;
	
	private String searchTerm;
	private int nearbyLocationsCount = 5;
	private Marker currMarker;
	private float currZoomLevel = 16;
	private boolean enablePanAndZoom = false;
	private HashMap<Marker, JSONObject> markerHashMap;
	
	private MMOnSearchTextFragmentCompleteListener onSearchTextFragmentCompleteListener;
	private MMOnNearbyLocationsItemClickListener onNearbyLocationsFragmentItemClickListener;
	private MMOnHistoryFragmentItemClickListener onHistFragmentItemClickListener;
	private MMOnCategoryFragmentItemClickListener onCategoryFragmentItemClickListener;
	
	private boolean retrieveNearbyLocations = true;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		View view = inflater.inflate(R.layout.fragment_searchlocations_screen, container, false);
		llCreateHotSpot = (LinearLayout) view.findViewById(R.id.llcreatehotspot);
		etSearch = (EditText) view.findViewById(R.id.etsearch);
		svNearbyLocations = (MMScrollView) view.findViewById(R.id.svnearbylocations);
		elvNearbyLocations = (MMExpandedNearbyLocationsListView) view.findViewById(R.id.enllvnearbylocations);
		llLoadMore = (LinearLayout) view.findViewById(R.id.llloadmore);
		tvHoldToPanAndZoom = (TextView) view.findViewById(R.id.tvholdtopanandzoom);
		smfNearbyLocations = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragnearbylocationsmap);
		elvSearch = (MMExpandedListView) view.findViewById(R.id.elvsearch);
		googleMap = smfNearbyLocations.getMap();
		
		markerHashMap = new HashMap<Marker, JSONObject>();
		
		llCreateHotSpot.setOnClickListener(SearchLocationsFragment.this);
		elvNearbyLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				try {
					Log.d(TAG, TAG + "onItemClick");
					addToHistory(nearbyLocations.getJSONObject(position));
					
					onNearbyLocationsFragmentItemClickListener.onNearbyLocationsItemClick(nearbyLocations.getJSONObject(position));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		llLoadMore.setOnClickListener(SearchLocationsFragment.this);
		if(!enablePanAndZoom) {
			tvHoldToPanAndZoom.setText(R.string.tv_hold_to_enable_pan_and_zoom);
		} else {
			tvHoldToPanAndZoom.setText(R.string.tv_hold_to_disable_pan_and_zoom);
		}
		elvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				switch(position) {
					case 0:
						onHistFragmentItemClickListener.onHistoryItemClick();
						break;
					case 1:
						onCategoryFragmentItemClickListener.onCategoryFragmentItemClick(getActivity().getString(R.string.tv_title_categories), MMCategories.getTopLevelCategories(getActivity()), true);
						break;
				}
			}
		});
		
		panAndZoom();
		
		try {
			if(retrieveNearbyLocations) {
				if(MMLocationManager.isGPSEnabled() && (location = MMLocationManager.getGPSLocation(new MMLocationListener())) != null) {
					searchAllNearbyLocations();
				}
			} else {
				setNearbyLocations();
			}
			getLocationHistory();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setSearchByText();
		setSearch();
		
		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
//		if(activity instanceof MMOnSearchTextFragmentCompleteListener) {
//			onSearchTextFragmentCompleteListener = (MMOnSearchTextFragmentCompleteListener) activity;
			if(activity instanceof MMOnNearbyLocationsItemClickListener) {
				onNearbyLocationsFragmentItemClickListener = (MMOnNearbyLocationsItemClickListener) activity;
				if(activity instanceof MMOnHistoryFragmentItemClickListener) {
					onHistFragmentItemClickListener = (MMOnHistoryFragmentItemClickListener) activity;
					if(activity instanceof MMOnCategoryFragmentItemClickListener) {
						onCategoryFragmentItemClickListener = (MMOnCategoryFragmentItemClickListener) activity;
					}
				}
			}
//		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.llcreatehotspot:
				break;
			case R.id.llloadmore:
				try {
					nearbyLocationsCount += 5;
					setNearbyLocations();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.TextView.OnEditorActionListener#onEditorAction(android.widget.TextView, int, android.view.KeyEvent)
	 */
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		Log.d(TAG, TAG + "actionId: " + actionId);
		if(actionId == EditorInfo.IME_ACTION_SEARCH) {
			searchTerm = etSearch.getText().toString();
//			searchByText();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.google.android.gms.maps.GoogleMap.OnMapLongClickListener#onMapLongClick(com.google.android.gms.maps.model.LatLng)
	 */
	@Override
	public void onMapLongClick(LatLng latLng) {
		if(!enablePanAndZoom) {
			tvHoldToPanAndZoom.setText(R.string.tv_hold_to_disable_pan_and_zoom);
			enablePanAndZoom = true;
			svNearbyLocations.requestDisallowInterceptTouchEvent(true);
			svNearbyLocations.setDisableStatus(true);
//			smfNearbyLocations.getView().getParent().getParent().requestDisallowInterceptTouchEvent(true);
//			svNearbyLocations.getParent().requestDisallowInterceptTouchEvent(true);
//			svNearbyLocations.getParent().requestDisallowInterceptTouchEvent(true);
//			svNearbyLocations.setOnTouchListener(new OnTouchListener() {
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					Log.d(TAG, TAG + "scrollView onTouch");
//					return true;
//				}
//			});
			panAndZoom();
		} else {
			tvHoldToPanAndZoom.setText(R.string.tv_hold_to_enable_pan_and_zoom);
			enablePanAndZoom = false;
			svNearbyLocations.setDisableStatus(false);
//			svNearbyLocations.requestDisallowInterceptTouchEvent(false);
//			svNearbyLocations.setOnTouchListener(null);
			panAndZoom();
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
			onNearbyLocationsFragmentItemClickListener.onNearbyLocationsItemClick(markerHashMap.get((Marker) marker));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		Log.d(TAG, "onDestroyView");
		super.onDestroyView();
		try {
			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
			transaction.remove(smfNearbyLocations);
			transaction.commit();
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
	private void setSearchByText() {
		etSearch.setOnEditorActionListener(SearchLocationsFragment.this);
		
		if(!MMLocationManager.isGPSEnabled() || MMLocationManager.getGPSLocation(new MMLocationListener()) == null) {
			etSearch.setFocusable(false);
			etSearch.setFocusableInTouchMode(false);
			etSearch.setClickable(false);
		}
	}
	
	private void setSearch() {
		String[] searchArray = getActivity().getResources().getStringArray(R.array.array_search);
		MMSearchCategoriesItem[] searchItems = new MMSearchCategoriesItem[searchArray.length];
		
		for(int i = 0; i < searchArray.length; i++) {
			searchItems[i] = new MMSearchCategoriesItem();
			searchItems[i].setCatIconId(0);
			searchItems[i].setCatName(searchArray[i]);
			searchItems[i].setCatIndicatorIconId(R.drawable.listview_accessory_indicator);
		}
		
		ArrayAdapter<MMSearchCategoriesItem> arrayAdapter = new MMSearchCategoriesArrayAdapter(getActivity(), R.layout.listview_row_searchcategory, searchItems);
		elvSearch.setAdapter(arrayAdapter);
	}
	
	/**
	 * 
	 */
	private void searchByText() {
		MMSearchLocationAdapter.searchLocationWithText(new SearchTextCallback(), 
													   MMLocationManager.getLocationLatitude(),
													   MMLocationManager.getLocationLongitude(),
													   userPrefs.getInt(MMSDKConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMSDKConstants.SEARCH_RADIUS_HALF_MILE),
													   searchTerm,
													   MMConstants.PARTNER_ID,
													   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
													   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
		MMProgressDialog.displayDialog(getActivity(),
									   MMSDKConstants.DEFAULT_STRING_EMPTY,
									   getString(R.string.pd_search_for) + MMSDKConstants.DEFAULT_STRING_SPACE + searchTerm + getString(R.string.pd_ellipses));
    	InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
	}
	
	/**
	 * 
	 */
	private void searchAllNearbyLocations() {
		MMSearchLocationAdapter.searchAllNearby(new SearchCallback(),
												MMLocationManager.getLocationLatitude(),
												MMLocationManager.getLocationLongitude(),
												userPrefs.getInt(MMSDKConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMSDKConstants.SEARCH_RADIUS_HALF_MILE),
												MMConstants.PARTNER_ID,
												userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
												userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
		MMProgressDialog.displayDialog(getActivity(),
									   MMSDKConstants.DEFAULT_STRING_EMPTY,
									   getString(R.string.pd_search_all_nearby));
	}
	
	private void panAndZoom() {
		UiSettings uiSettings = googleMap.getUiSettings();
		uiSettings.setCompassEnabled(enablePanAndZoom);
		uiSettings.setMyLocationButtonEnabled(enablePanAndZoom);
		uiSettings.setRotateGesturesEnabled(enablePanAndZoom);
		uiSettings.setScrollGesturesEnabled(enablePanAndZoom);
		uiSettings.setTiltGesturesEnabled(enablePanAndZoom);
		uiSettings.setZoomControlsEnabled(enablePanAndZoom);
		uiSettings.setZoomGesturesEnabled(enablePanAndZoom);
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
	private void setNearbyLocations() throws JSONException {
		int nearbyLocationsToDisplay = 0;
		if(nearbyLocationsCount >= nearbyLocations.length()) {
			nearbyLocationsToDisplay = nearbyLocations.length();
			llLoadMore.setVisibility(View.GONE);
		} else {
			nearbyLocationsToDisplay = nearbyLocationsCount;
		}
		
		MMSearchResultsItem[] resultLocations = new MMSearchResultsItem[nearbyLocationsToDisplay];
		for(int i = 0; i < nearbyLocationsToDisplay; i++) {
			JSONObject jObj = nearbyLocations.getJSONObject(i);
			resultLocations[i] = new MMSearchResultsItem();
			resultLocations[i].setLocName(jObj.getString(MMSDKConstants.JSON_KEY_NAME));
			resultLocations[i].setLocDist(MMUtility.calcDist(location, jObj.getDouble(MMSDKConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE)) + MMSDKConstants.DEFAULT_STRING_SPACE + 
					getString(R.string.miles));
			resultLocations[i].setLocAddr(jObj.getString(MMSDKConstants.JSON_KEY_ADDRESS) + MMSDKConstants.DEFAULT_STRING_NEWLINE + jObj.getString(MMSDKConstants.JSON_KEY_LOCALITY) + MMSDKConstants.DEFAULT_STRING_COMMA_SPACE + 
									jObj.getString(MMSDKConstants.JSON_KEY_REGION));
			Log.d(TAG, i + " stream: " + jObj.getInt(MMSDKConstants.MEDIA_LIVESTREAMING) + " video: " + jObj.getInt(MMSDKConstants.JSON_KEY_VIDEOS) + " images: " + jObj.getInt(MMSDKConstants.JSON_KEY_IMAGES));
		}
		
		ArrayAdapter<MMSearchResultsItem> arrayAdapter = new MMSearchResultsArrayAdapter(getActivity(), R.layout.listview_row_searchresults, resultLocations);
		elvNearbyLocations.setAdapter(arrayAdapter);
		addToGoogleMap();
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void addToGoogleMap() throws JSONException {
		LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
		googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		
		int nearbyLocationsToDisplay = 0;
		if(nearbyLocationsCount >= nearbyLocations.length()) {
			nearbyLocationsToDisplay = nearbyLocations.length();
		} else {
			nearbyLocationsToDisplay = nearbyLocationsCount;
		}
		
		for(int i = 0; i < nearbyLocationsToDisplay; i++) {
			JSONObject jObj = nearbyLocations.getJSONObject(i);
			
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
		googleMap.setOnInfoWindowClickListener(SearchLocationsFragment.this);
		googleMap.setOnMapLongClickListener(SearchLocationsFragment.this);
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
	
	private class SearchTextCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					onSearchTextFragmentCompleteListener.onSearchTextComplete(searchTerm, (String) obj);
				}
			}
		}
	}
	
    /**
     * Custom {@link MMCallback} specifically for {@link SearchScreen} to be processed after receiving response from MobMonkey server.
     * @author Dezapp, LLC
     *
     */
	private class SearchCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				Log.d(TAG, TAG + "Response: " + ((String) obj));
				
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						retrieveNearbyLocations = false;
						nearbyLocations = new JSONArray((String) obj);
						setNearbyLocations();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
//				try {
//					JSONObject jObj = new JSONObject((String) obj);
//					mmNoCategoryItemClickFragmentListener.onNoCategoryFragmentItemClick(0, searchCategory, jObj.getJSONArray(MMSDKConstants.JSON_KEY_DEFAULT_TEXTS).toString());
//					mmNoCategoryItemClickFragmentListener.onNoCategoryFragmentItemClick(0, searchCategory, (String) obj);
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
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
