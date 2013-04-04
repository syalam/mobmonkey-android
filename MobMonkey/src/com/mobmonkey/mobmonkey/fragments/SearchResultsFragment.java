/**
 * 
 */
package com.mobmonkey.mobmonkey.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkey.utils.MMResultsLocation;
import com.mobmonkey.mobmonkey.utils.MMSearchResultsArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMUtility;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationListener;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationManager;

/**
 * @author dezapp1
 *
 */
public class SearchResultsFragment extends MMFragment implements OnClickListener, OnItemClickListener, OnInfoWindowClickListener {
	private static final String TAG = "SearchResultsScreen: ";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	
	private JSONArray searchResults;
	private Location location;
	private MMResultsLocation[] favorites;
	private JSONArray locationHistory;
	
	private TextView tvSearchResultsTitle;
	private ImageButton ibMap;
	private Button btnAddLocClear;
	private ListView lvSearchResults;
	private SupportMapFragment smfResultLocations;
	private GoogleMap googleMap;
	
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
		btnAddLocClear = (Button) view.findViewById(R.id.btnaddlocclear);
		smfResultLocations = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmap);
		lvSearchResults = (ListView) view.findViewById(R.id.lvsearchresults);
		
		tvSearchResultsTitle.setText(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE));
		
		if(!getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS).equals(MMAPIConstants.DEFAULT_STRING)) {
			try {
				searchResults = new JSONArray(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS));
				getLocations();
				displayMap();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			searchResults = new JSONArray();
		}
		
		ibMap.setOnClickListener(SearchResultsFragment.this);
		btnAddLocClear.setOnClickListener(SearchResultsFragment.this);
		ArrayAdapter<MMResultsLocation> arrayAdapter = new MMSearchResultsArrayAdapter(getActivity(), R.layout.search_result_list_row, favorites);
		lvSearchResults.setAdapter(arrayAdapter);
		lvSearchResults.setOnItemClickListener(SearchResultsFragment.this);
		
		smfResultLocations.getView().setVisibility(View.INVISIBLE);
		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnSearchResultsLocationSelectListener) {
			searchResultsLocationSelectListener = (OnSearchResultsLocationSelectListener) activity;
		}
	}
	
	@Override
	public void onInfoWindowClick(Marker marker) {
		try {
			JSONObject jObj = markerHashMap.get((Marker) marker);
			addToHistory(jObj);
			
			searchResultsLocationSelectListener.onLocationSelect(markerHashMap.get((Marker) marker));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.ibmap:
				if(lvSearchResults.getVisibility() == View.VISIBLE) {
					lvSearchResults.setVisibility(View.INVISIBLE);
					smfResultLocations.getView().setVisibility(View.VISIBLE);
				} else if(lvSearchResults.getVisibility() == View.INVISIBLE) {
					lvSearchResults.setVisibility(View.VISIBLE);
					smfResultLocations.getView().setVisibility(View.INVISIBLE);
				}
				break;
			case R.id.btnaddlocclear:
				if(MMLocationManager.isGPSEnabled()) {
					startActivity(new Intent(getActivity(), AddLocationScreen.class));
				}
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
			favorites = new MMResultsLocation[searchResults.length()];
			for(int i = 0; i < searchResults.length(); i++) {
				JSONObject jObj = searchResults.getJSONObject(i);
				favorites[i] = new MMResultsLocation();
				favorites[i].setLocName(jObj.getString(MMAPIConstants.JSON_KEY_NAME));
				favorites[i].setLocDist(MMUtility.calcDist(location, jObj.getDouble(MMAPIConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMAPIConstants.JSON_KEY_LONGITUDE)) + MMAPIConstants.DEFAULT_SPACE + getString(R.string.miles));
				favorites[i].setLocAddr(jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS) + MMAPIConstants.DEFAULT_NEWLINE + jObj.getString(MMAPIConstants.JSON_KEY_LOCALITY) + MMAPIConstants.COMMA_SPACE + 
										jObj.getString(MMAPIConstants.JSON_KEY_REGION) + MMAPIConstants.COMMA_SPACE + jObj.getString(MMAPIConstants.JSON_KEY_POSTCODE));
			}
	}
	
	private void displayMap() throws JSONException {
		if(getArguments().getBoolean(MMAPIConstants.KEY_INTENT_EXTRA_DISPLAY_MAP, true)) {
			googleMap = smfResultLocations.getMap();
			markerHashMap = new HashMap<Marker, JSONObject>();
			addToGoogleMap();
			getLocationHistory();
		} else {
			ibMap.setVisibility(View.GONE);
			btnAddLocClear.setBackgroundResource(R.drawable.orange_button_background);
			btnAddLocClear.setText(R.string.btn_clear);
			
			if(!getLocationHistory()) {
				displayNoHistoryAlert();
			}
		}
	}
	
	private boolean getLocationHistory() throws JSONException {
		String history = userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_HISTORY, MMAPIConstants.DEFAULT_STRING);
		if(!history.equals(MMAPIConstants.DEFAULT_STRING)) {
			locationHistory = new JSONArray(history);
			return true;
		} else {
			locationHistory = new JSONArray();
			return false;
		}
	}
	
	private void addToGoogleMap() throws JSONException {		
		for(int i = 0; i < searchResults.length(); i++) {
			JSONObject jObj = searchResults.getJSONObject(i);
			
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
		googleMap.setOnInfoWindowClickListener(SearchResultsFragment.this);
		googleMap.setMyLocationEnabled(true);
	}
	
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
