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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMNearbyLocationsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMSearchCategoriesArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMSearchCategoriesItem;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnCategoryFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnCreateHotSpotFragmentClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnHistoryFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnNearbyLocationsItemClickListener;
import com.mobmonkey.mobmonkeyandroid.utils.MMCategories;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedNearbyLocationsListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMScrollView;
import com.mobmonkey.mobmonkeyandroid.utils.MMSubLocations;
import com.mobmonkey.mobmonkeyandroid.utils.MMSupportMapFragment;
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
																   OnLongClickListener,
																   OnTouchListener,
																   OnInfoWindowClickListener,
																   OnMapLongClickListener {
	private static final String TAG = "SearchLocationsFragment: ";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	private FragmentManager fragmentManager;
	
	private Location location;
	private JSONArray nearbyLocations;
	private JSONArray locationHistory;
	
	private TextView tvNavBarTitle;
	private LinearLayout llCreateHotSpot;
	private EditText etSearch;
	private Button btnCancel;
	private MMScrollView svNearbyLocations;
	private MMExpandedNearbyLocationsListView enllvNearbyLocations;
	private LinearLayout llLoadMore;
	private TextView tvHoldToPanAndZoom;
	private MMExpandedListView elvSearch;
	private LinearLayout llNearbyLocationsSearch;
	private TextView tvNearbyLocationsSearch;
	private ListView lvNearbyLocationsSearch;
	
	private MMSupportMapFragment smfNearbyLocations;
	private GoogleMap googleMap;
	
	private MMNearbyLocationsArrayAdapter nearbyLocationsArrayAdapter;
	private MMNearbyLocationsArrayAdapter nearbyLocationsSearchArrayAdapter; 
	private InputMethodManager inputMethodManager;
	
	private int nearbyLocationsCount = 5;
	private Marker currMarker;
	private float currZoomLevel = 16;
	private boolean enablePanAndZoom = false;
	private HashMap<Marker, JSONObject> markerHashMap;
	
	private MMOnCreateHotSpotFragmentClickListener createHotSpotFragmentClicKlistener;
	private MMOnNearbyLocationsItemClickListener nearbyLocationsFragmentItemClickListener;
	private MMOnHistoryFragmentItemClickListener historyFragmentItemClickListener;
	private MMOnCategoryFragmentItemClickListener categoryFragmentItemClickListener;
	
	private boolean nearbyLocationsSearch = false;
	private String searchText = MMSDKConstants.DEFAULT_STRING_EMPTY;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		fragmentManager = getFragmentManager();
		
		View view = inflater.inflate(R.layout.fragment_searchlocations_screen, container, false);
		tvNavBarTitle = (TextView) view.findViewById(R.id.tvnavbartitle);
		llCreateHotSpot = (LinearLayout) view.findViewById(R.id.llcreatehotspot);
		etSearch = (EditText) view.findViewById(R.id.etsearch);
		btnCancel = (Button) view.findViewById(R.id.btncancel);
		svNearbyLocations = (MMScrollView) view.findViewById(R.id.svnearbylocations);
		enllvNearbyLocations = (MMExpandedNearbyLocationsListView) view.findViewById(R.id.enllvnearbylocations);
		llLoadMore = (LinearLayout) view.findViewById(R.id.llloadmore);
		tvHoldToPanAndZoom = (TextView) view.findViewById(R.id.tvholdtopanandzoom);
//		smfNearbyLocations = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragnearbylocationsmap);
		elvSearch = (MMExpandedListView) view.findViewById(R.id.elvsearch);
		llNearbyLocationsSearch = (LinearLayout) view.findViewById(R.id.llnearbylocationssearch);
		tvNearbyLocationsSearch = (TextView) view.findViewById(R.id.tvnearbylocationssearch);
		lvNearbyLocationsSearch = (ListView) view.findViewById(R.id.lvnearbylocationssearch);
//		googleMap = smfNearbyLocations.getMap();
		
		inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		markerHashMap = new HashMap<Marker, JSONObject>();
		
		llCreateHotSpot.setOnClickListener(SearchLocationsFragment.this);
		btnCancel.setOnClickListener(SearchLocationsFragment.this);
		enllvNearbyLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				try {
					Log.d(TAG, TAG + "onItemClick");
					addToHistory(nearbyLocationsArrayAdapter.getItem(position));					
					nearbyLocationsFragmentItemClickListener.onNearbyLocationsItemClick(nearbyLocationsArrayAdapter.getItem(position));
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
						historyFragmentItemClickListener.onHistoryItemClick();
						break;
					case 1:
						categoryFragmentItemClickListener.onCategoryFragmentItemClick(getActivity().getString(R.string.tv_title_categories), MMCategories.getTopLevelCategories(getActivity()), true);
						break;
				}
			}
		});
		lvNearbyLocationsSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				try {
					addToHistory(nearbyLocationsSearchArrayAdapter.getItem(position));
					nearbyLocationsFragmentItemClickListener.onNearbyLocationsItemClick(nearbyLocationsSearchArrayAdapter.getItem(position));
					inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
					nearbyLocationsSearch = true;
					searchText = etSearch.getText().toString();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		
//		panAndZoom();
		
		try {
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
		if(activity instanceof MMOnCreateHotSpotFragmentClickListener) {
			createHotSpotFragmentClicKlistener = (MMOnCreateHotSpotFragmentClickListener) activity;
			if(activity instanceof MMOnNearbyLocationsItemClickListener) {
				nearbyLocationsFragmentItemClickListener = (MMOnNearbyLocationsItemClickListener) activity;
				if(activity instanceof MMOnHistoryFragmentItemClickListener) {
					historyFragmentItemClickListener = (MMOnHistoryFragmentItemClickListener) activity;
					if(activity instanceof MMOnCategoryFragmentItemClickListener) {
						categoryFragmentItemClickListener = (MMOnCategoryFragmentItemClickListener) activity;
					}
				}
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
			case R.id.llcreatehotspot:
				createHotSpotFragmentClicKlistener.onCreateHotSpotClick(nearbyLocations);
				break;
			case R.id.etsearch:
				setNearbyLocationsSearch();
				break;
			case R.id.btncancel:
				cancelNearbyLocationsSearch();
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

	/* (non-Javadoc)
	 * @see android.view.View.OnLongClickListener#onLongClick(android.view.View)
	 */
	@Override
	public boolean onLongClick(View v) {
		switch(v.getId()) {
			case R.id.etsearch:
				setNearbyLocationsSearch();
				break;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()) {
			case R.id.etsearch:
				tvNavBarTitle.setVisibility(View.GONE);
				return true;
		}
		return false;
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
			nearbyLocationsFragmentItemClickListener.onNearbyLocationsItemClick(markerHashMap.get((Marker) marker));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, TAG + "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.d(TAG, TAG + "onResume");
		super.onResume();
		if(MMLocationManager.isGPSEnabled() && (location = MMLocationManager.getGPSLocation(new MMLocationListener())) != null) {
			searchAllNearbyLocations();
			getMMSupportMapFragment();
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		Log.d(TAG, TAG + "onStart");
		super.onStart();
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
			transaction.remove(smfNearbyLocations);
			transaction.commitAllowingStateLoss();
		} catch (Exception e) {
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
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		Log.d(TAG, TAG + "onFragmentBackPressed");
		if(llNearbyLocationsSearch.getVisibility() == View.VISIBLE) {
			cancelNearbyLocationsSearch();
		}
	}
	
	/**
	 * 
	 */
	private void setSearchByText() {
//		etSearch.setOnTouchListener(SearchLocationsFragment.this);
		etSearch.setOnClickListener(SearchLocationsFragment.this);
		etSearch.setOnLongClickListener(SearchLocationsFragment.this);
		etSearch.addTextChangedListener(new NearbyLocationsTextWatcher());
//		etSearch.setKeyListener(new NearbyLocationsKeyListener());
		
		if(!MMLocationManager.isGPSEnabled() || MMLocationManager.getGPSLocation(new MMLocationListener()) == null) {
			etSearch.setFocusable(false);
			etSearch.setFocusableInTouchMode(false);
			etSearch.setClickable(false);
		}
	}
	
	/**
	 * 
	 */
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
	
	/**
	 * 
	 */
	private void getMMSupportMapFragment() {
		smfNearbyLocations = (MMSupportMapFragment) fragmentManager.findFragmentByTag(MMSDKConstants.MMSUPPORT_MAP_FRAGMENT_TAG);
		if(smfNearbyLocations == null) {
			smfNearbyLocations = MMSupportMapFragment.newInstance();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.add(R.id.flnearbylocationsmap, smfNearbyLocations, MMSDKConstants.MMSUPPORT_MAP_FRAGMENT_TAG);
			fragmentTransaction.commit();
		}
		if(googleMap == null) {
			googleMap = smfNearbyLocations.getMap();
			if(googleMap != null) {
				panAndZoom();
			}
		}
	}
	
	/**
	 * 
	 */
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
		if(nearbyLocationsCount >= nearbyLocations.length()) {
			nearbyLocationsCount = nearbyLocations.length();
			llLoadMore.setVisibility(View.GONE);
		}
		
		ArrayList<JSONObject> resultLocations = new ArrayList<JSONObject>();
		for(int i = 0; i < nearbyLocationsCount; i++) {
			resultLocations.add(nearbyLocations.getJSONObject(i));
		}
		
		nearbyLocationsArrayAdapter = new MMNearbyLocationsArrayAdapter(getActivity(), R.layout.listview_row_searchresults, resultLocations);
		enllvNearbyLocations.setAdapter(nearbyLocationsArrayAdapter);
		addToGoogleMap();
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void setSearchNearbyLocations() throws JSONException {
		ArrayList<JSONObject> resultLocations = new ArrayList<JSONObject>();
		for(int i = 0; i < nearbyLocations.length(); i++) {
			resultLocations.add(nearbyLocations.getJSONObject(i));
		}
		nearbyLocationsSearchArrayAdapter = new MMNearbyLocationsArrayAdapter(getActivity(), R.layout.listview_row_searchresults, resultLocations);
		lvNearbyLocationsSearch.setAdapter(nearbyLocationsSearchArrayAdapter);
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void addToGoogleMap() throws JSONException {
		LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
		googleMap.clear();
		googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		markerHashMap.clear();
		
		if(nearbyLocationsCount >= nearbyLocations.length()) {
			nearbyLocationsCount = nearbyLocations.length();
		}
		
		for(int i = 0; i < nearbyLocationsCount; i++) {
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
	
	/**
	 * 
	 */
	private void setNearbyLocationsSearch() {
		nearbyLocationsSearch = true;
		tvNavBarTitle.setVisibility(View.GONE);
		btnCancel.setVisibility(View.VISIBLE);
		llNearbyLocationsSearch.setVisibility(View.VISIBLE);
		svNearbyLocations.setDisableStatus(true);
		svNearbyLocations.setClickable(false);
		enllvNearbyLocations.setEnabled(false);
	}
	
	/**
	 * 
	 */
	private void cancelNearbyLocationsSearch() {
		nearbyLocationsSearch = false;
		searchText = MMSDKConstants.DEFAULT_STRING_EMPTY;
		tvNavBarTitle.setVisibility(View.VISIBLE);
		btnCancel.setVisibility(View.GONE);
		llNearbyLocationsSearch.setVisibility(View.GONE);
		svNearbyLocations.setDisableStatus(false);
		svNearbyLocations.setClickable(true);
		enllvNearbyLocations.setEnabled(true);
		etSearch.setText(MMSDKConstants.DEFAULT_STRING_EMPTY);
		inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
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
						nearbyLocations = MMUtility.filterSubLocations((String) obj);
						nearbyLocationsCount = 5;
						setNearbyLocations();
						setSearchNearbyLocations();
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
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class NearbyLocationsTextWatcher implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(nearbyLocationsArrayAdapter != null) {
				if(s.length() > 0) {
					tvNearbyLocationsSearch.setVisibility(View.VISIBLE);
					lvNearbyLocationsSearch.setVisibility(View.VISIBLE);
					nearbyLocationsSearchArrayAdapter.getFilter().filter(s);
				} else {
					tvNearbyLocationsSearch.setVisibility(View.GONE);
					lvNearbyLocationsSearch.setVisibility(View.GONE);
				}
			}
		}
	}
}
