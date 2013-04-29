package com.mobmonkey.mobmonkeyandroid.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.AddLocationScreen;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMSearchResultsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMSearchResultsItem;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;

/**
 * @author Dezapp, LLC
 *
 */
public class FavoritesFragment extends MMFragment implements OnClickListener, OnItemClickListener {//, OnInfoWindowClickListener {
	private static final String TAG = "FavoritesFragment: ";
	
	private SharedPreferences userPrefs;
	private Location location;
	
	private ImageButton ibMap;
	private Button btnAddLoc;
	private ListView lvFavorites;
	
	private MMSearchResultsItem[] favoriteLocations;
	private JSONArray favoritesList;
	
	private MMOnMapIconFragmentClickListener mapIconClickListener;
	private MMOnSearchResultsFragmentItemClickListener locationSelectListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, TAG + "onCreateView");
		
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		View view = inflater.inflate(R.layout.fragment_favorites_screen, container, false);
		ibMap = (ImageButton) view.findViewById(R.id.ibmap);
		btnAddLoc = (Button) view.findViewById(R.id.btnaddloc);
		lvFavorites = (ListView) view.findViewById(R.id.lvbookmarks);

		ibMap.setOnClickListener(FavoritesFragment.this);
		btnAddLoc.setOnClickListener(FavoritesFragment.this);
		lvFavorites.setOnItemClickListener(FavoritesFragment.this);
		
		return view;
	}

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

	@Override
	public void onClick(View view) {
		if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
			switch(view.getId()) {
				case R.id.ibmap:
					mapIconClickListener.onMapIconFragmentClick(MMSDKConstants.FAVORITES_FRAGMENT_MAP);
					break;
				case R.id.btnaddloc:
					startActivity(new Intent(getActivity(), AddLocationScreen.class));
					break;
			}
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		try {
			locationSelectListener.onSearchResultsFragmentItemClick(favoritesList.getJSONObject(position));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
			try {
				refreshFavorites();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
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
		getFavorites();
		ArrayAdapter<MMSearchResultsItem> arrayAdapter = new MMSearchResultsArrayAdapter(getActivity(), R.layout.listview_row_searchresults, favoriteLocations);
		lvFavorites.setAdapter(arrayAdapter);
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void getFavorites() throws JSONException {
		favoriteLocations = new MMSearchResultsItem[favoritesList.length()];
		for(int i = 0; i < favoritesList.length(); i++) {
			JSONObject jObj = favoritesList.getJSONObject(i);
			favoriteLocations[i] = new MMSearchResultsItem();
			favoriteLocations[i].setLocName(jObj.getString(MMSDKConstants.JSON_KEY_NAME));
			favoriteLocations[i].setLocDist(MMUtility.calcDist(location, jObj.getDouble(MMSDKConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE)) + getString(R.string.miles));
			favoriteLocations[i].setLocAddr(jObj.getString(MMSDKConstants.JSON_KEY_ADDRESS) + MMSDKConstants.DEFAULT_STRING_NEWLINE + jObj.getString(MMSDKConstants.JSON_KEY_LOCALITY) + 
											MMSDKConstants.DEFAULT_STRING_COMMA_SPACE + jObj.getString(MMSDKConstants.JSON_KEY_REGION) + MMSDKConstants.DEFAULT_STRING_COMMA_SPACE + jObj.getString(MMSDKConstants.JSON_KEY_POSTCODE));
		}
		
		// reverse array
		List temp = Arrays.asList(favoriteLocations);
		Collections.reverse(temp);
		favoriteLocations = (MMSearchResultsItem[]) temp.toArray();
		
		temp = new ArrayList<JSONObject>();
		for(int i = 0; i < favoritesList.length(); i++) {
			temp.add(favoritesList.get(i));
		}
		Collections.reverse(temp);
		favoritesList = new JSONArray(temp);
	}
}
