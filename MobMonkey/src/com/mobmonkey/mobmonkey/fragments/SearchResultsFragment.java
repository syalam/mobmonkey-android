/**
 * 
 */
package com.mobmonkey.mobmonkey.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.SearchResultDetailsScreen;
import com.mobmonkey.mobmonkey.SearchResultsScreen;
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
public class SearchResultsFragment extends MMFragment implements OnItemClickListener {
	private static final String TAG = "SearchResultsScreen: ";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	
	private JSONArray searchResults;
	private Location location;
	private MMResultsLocation[] favorites;
	private JSONArray locationHistory;
	
	private TextView tvSearchResultsTitle;
	private ImageButton ibmap;
	private Button btnAddLocClear;
	private ListView lvSearchResults;
	private SupportMapFragment smfResultLocations;
	private GoogleMap googleMap;
	
	private HashMap<Marker, JSONObject> markerHashMap;
	private Intent locDetailsIntent;

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
		ibmap = (ImageButton) view.findViewById(R.id.ibmap);
		btnAddLocClear = (Button) view.findViewById(R.id.btnaddlocclear);
//		smfResultLocations = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmap);
		lvSearchResults = (ListView) view.findViewById(R.id.lvsearchresults);
		
		tvSearchResultsTitle.setText(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE));
		
		if(!getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS).equals(MMAPIConstants.DEFAULT_STRING)) {
			try {
				searchResults = new JSONArray(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS));
				getLocations();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			searchResults = new JSONArray();
		}
		
		ArrayAdapter<MMResultsLocation> arrayAdapter = new MMSearchResultsArrayAdapter(getActivity(), R.layout.search_result_list_row, favorites);
		lvSearchResults.setAdapter(arrayAdapter);
		lvSearchResults.setOnItemClickListener(SearchResultsFragment.this);
		
		return view;
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
			
			locDetailsIntent = new Intent(getActivity(), SearchResultDetailsScreen.class);
			locDetailsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, searchResults.getJSONObject(position).toString());
			startActivity(locDetailsIntent);
		} catch (JSONException e) {
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
}
