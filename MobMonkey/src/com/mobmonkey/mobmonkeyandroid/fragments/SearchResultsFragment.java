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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;
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
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMResultsLocation;
import com.mobmonkey.mobmonkeyandroid.utils.MMSearchResultsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
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
	private MMResultsLocation[] resultLocations;
	private JSONArray locationHistory;
	
	private TextView tvSearchResultsTitle;
	private ImageButton ibMap;
	private Button btnAddLoc;
	private Button btnCancel;
	private Button btnClear;
	private ListView lvSearchResults;
	private SupportMapFragment smfResultLocations;
	private GoogleMap googleMap;
	private boolean displayMap = false;
	private boolean addLocClicked;
	private Marker currMarker;
	private float currZoomLevel = 16;
	
	private HashMap<Marker, JSONObject> markerHashMap;
	private OnSearchResultsLocationSelectListener searchResultsLocationSelectListener;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Context.MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		View view = inflater.inflate(R.layout.fragment_searchresults_screen, container, false);
		tvSearchResultsTitle = (TextView) view.findViewById(R.id.tvsearchresultstitle);
		ibMap = (ImageButton) view.findViewById(R.id.ibmap);
		btnAddLoc = (Button) view.findViewById(R.id.btnaddloc);
		btnCancel = (Button) view.findViewById(R.id.btncancel);
		btnClear = (Button) view.findViewById(R.id.btnclear);
		smfResultLocations = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmap);
		lvSearchResults = (ListView) view.findViewById(R.id.lvsearchresults);
		
		tvSearchResultsTitle.setText(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE));
		
		try {
			if(!getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS).equals(MMAPIConstants.DEFAULT_STRING_EMPTY)) {
				searchResults = new JSONArray(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS));
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
		btnClear.setOnClickListener(SearchResultsFragment.this);
		lvSearchResults.setOnItemClickListener(SearchResultsFragment.this);
		
		ArrayAdapter<MMResultsLocation> arrayAdapter = new MMSearchResultsArrayAdapter(getActivity(), R.layout.search_result_list_row, resultLocations);
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
		if(activity instanceof OnSearchResultsLocationSelectListener) {
			searchResultsLocationSelectListener = (OnSearchResultsLocationSelectListener) activity;
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
			searchResultsLocationSelectListener.onLocationSelect(markerHashMap.get((Marker) marker));
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
			try{
				Address locationClicked = getAddressForLocation(pointClicked.latitude, pointClicked.longitude);
				
				// pass information to category screen
				Bundle bundle = new Bundle();
				bundle.putString(MMAPIConstants.JSON_KEY_ADDRESS, locationClicked.getAddressLine(0));
				bundle.putString(MMAPIConstants.JSON_KEY_LOCALITY, locationClicked.getLocality());
				bundle.putString(MMAPIConstants.JSON_KEY_REGION, locationClicked.getAdminArea());
				bundle.putString(MMAPIConstants.JSON_KEY_POSTCODE, locationClicked.getPostalCode());
				bundle.putString(MMAPIConstants.JSON_KEY_LATITUDE, locationClicked.getLatitude()+"");
				bundle.putString(MMAPIConstants.JSON_KEY_LONGITUDE, locationClicked.getLongitude()+"");
				
				Intent intent = new Intent(getActivity(), AddLocationScreen.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}catch(IOException e)
			{
				e.printStackTrace();
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
				ibMapClick();
				break;
			case R.id.btnaddloc:
				buttonAddLocClick();
				break;
			case R.id.btncancel:
				buttonCancelClick();
				break;
			case R.id.btnclear:
				promptClearHistory();
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
				searchResultsLocationSelectListener.onLocationSelect(searchResults.getJSONObject(position));
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
		resultLocations = new MMResultsLocation[searchResults.length()];
			for(int i = 0; i < searchResults.length(); i++) {
				JSONObject jObj = searchResults.getJSONObject(i);
				resultLocations[i] = new MMResultsLocation();
				resultLocations[i].setLocName(jObj.getString(MMAPIConstants.JSON_KEY_NAME));
				resultLocations[i].setLocDist(MMUtility.calcDist(location, jObj.getDouble(MMAPIConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMAPIConstants.JSON_KEY_LONGITUDE)) + MMAPIConstants.DEFAULT_STRING_SPACE + 
						getString(R.string.miles));
				resultLocations[i].setLocAddr(jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS) + MMAPIConstants.DEFAULT_STRING_NEWLINE + jObj.getString(MMAPIConstants.JSON_KEY_LOCALITY) + MMAPIConstants.DEFAULT_STRING_COMMA_SPACE + 
										jObj.getString(MMAPIConstants.JSON_KEY_REGION) + MMAPIConstants.DEFAULT_STRING_COMMA_SPACE + jObj.getString(MMAPIConstants.JSON_KEY_POSTCODE));
			}
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void displayMap() throws JSONException {
		if(getArguments().getBoolean(MMAPIConstants.KEY_INTENT_EXTRA_DISPLAY_MAP, true)) {
			googleMap = smfResultLocations.getMap();
			markerHashMap = new HashMap<Marker, JSONObject>();
			addToGoogleMap();
			getLocationHistory();
		} else {
			ibMap.setVisibility(View.GONE);
			btnAddLoc.setVisibility(View.GONE);
			btnClear.setVisibility(View.VISIBLE);
			
			if(!getLocationHistory()) {
				displayNoHistoryAlert();
			}
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws JSONException
	 */
	private boolean getLocationHistory() throws JSONException {
		String history = userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_HISTORY, MMAPIConstants.DEFAULT_STRING_EMPTY);
		if(!history.equals(MMAPIConstants.DEFAULT_STRING_EMPTY)) {
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
			
			LatLng resultLocLatLng = new LatLng(jObj.getDouble(MMAPIConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMAPIConstants.JSON_KEY_LONGITUDE));
			
			Marker locationResultMarker = googleMap.addMarker(new MarkerOptions().
					position(resultLocLatLng).
					title(jObj.getString(MMAPIConstants.JSON_KEY_NAME))
					.snippet(jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS)));
			
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
	 */
	private void displayNoHistoryAlert() {
		new AlertDialog.Builder(getActivity())
			.setTitle(R.string.ad_title_no_history)
			.setMessage(R.string.ad_message_no_history)
			.setCancelable(false)
			.setNeutralButton(R.string.ad_btn_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//TODO: return to search fragment
				}
			})
			.show();
	}
	
	/**
	 * 
	 * @param position
	 * @throws JSONException
	 */
	private void addToHistory(JSONObject loc) throws JSONException {
		if(!locationExistsInHistory(loc)) {
			if(locationHistory.length() < MMAPIConstants.HISTORY_SIZE) {
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
				temp.remove(MMAPIConstants.HISTORY_SIZE);
				locationHistory = new JSONArray(temp);
			}
		}
		userPrefsEditor.putString(MMAPIConstants.SHARED_PREFS_KEY_HISTORY, locationHistory.toString());
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
			if(locationHistory.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_NAME).equals(loc.getString(MMAPIConstants.JSON_KEY_NAME)) &&
					locationHistory.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_LATITUDE).equals(loc.getString(MMAPIConstants.JSON_KEY_LATITUDE)) &&
					locationHistory.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_LONGITUDE).equals(loc.getString(MMAPIConstants.JSON_KEY_LONGITUDE))) {
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
			btnCancel.setVisibility(View.INVISIBLE);
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
				btnAddLoc.setVisibility(View.INVISIBLE);
				btnCancel.setVisibility(View.VISIBLE);
				
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ibMap.getLayoutParams();
				params.addRule(RelativeLayout.LEFT_OF, R.id.btncancel);
				ibMap.setLayoutParams(params);
			}
		}
	}
	
	/**
	 * 
	 */
	private void buttonCancelClick() {
		addLocClicked = false;
		btnAddLoc.setVisibility(View.VISIBLE);
		btnCancel.setVisibility(View.INVISIBLE);
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ibMap.getLayoutParams();
		params.addRule(RelativeLayout.LEFT_OF, R.id.btnaddloc);
		ibMap.setLayoutParams(params);
	}
	
	/**
	 * 
	 */
	private void promptClearHistory() {
		new AlertDialog.Builder(getActivity())
			.setTitle("clear history")
			.setMessage("Are you sure to clear all your history?")
			.setCancelable(false)
			.setPositiveButton(R.string.ad_btn_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					clearHistory();
				}
			})
			.setNegativeButton(R.string.ad_btn_no, null)
			.show();
		
	}
	
	/**
	 * 
	 */
	private void clearHistory() {
		resultLocations = new MMResultsLocation[0];
		ArrayAdapter<MMResultsLocation> arrayAdapter = new MMSearchResultsArrayAdapter(getActivity(), R.layout.search_result_list_row, resultLocations);
		lvSearchResults.setAdapter(arrayAdapter);
		lvSearchResults.invalidate();
		
		userPrefsEditor.remove(MMAPIConstants.SHARED_PREFS_KEY_HISTORY);
		userPrefsEditor.commit();
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	public interface OnSearchResultsLocationSelectListener {
		public void onLocationSelect(Object obj);
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
                titleUi.setText(MMAPIConstants.DEFAULT_STRING_EMPTY);
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null) {
                snippetUi.setText(snippet);
            } else {
                snippetUi.setText(MMAPIConstants.DEFAULT_STRING_EMPTY);
            }
        }
    }
}
