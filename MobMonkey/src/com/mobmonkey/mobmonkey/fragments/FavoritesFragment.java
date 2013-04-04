package com.mobmonkey.mobmonkey.fragments;

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
	
	private MMResultsLocation[] favoriteLocations;
	private JSONArray favoritesList;
	
	private OnMapIconClickListener mapIconClickListener;
	private OnMMLocationSelectListener locationSelectListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, TAG + "onCreateView");
		
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Context.MODE_PRIVATE);
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
		if(activity instanceof OnMapIconClickListener) {
			mapIconClickListener = (OnMapIconClickListener) activity;
			if(activity instanceof OnMMLocationSelectListener) {
				locationSelectListener = (OnMMLocationSelectListener) activity;
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.ibmap:
				if(MMLocationManager.isGPSEnabled()) {
					mapIconClickListener.onMapIconClicked(MMAPIConstants.FAVORITES_FRAGMENT_MAP);
				}
				break;
			case R.id.btnaddloc:
				if(MMLocationManager.isGPSEnabled()) {
					startActivity(new Intent(getActivity(), AddLocationScreen.class));
				}
				break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		try {
			locationSelectListener.onLocationSelect(favoritesList.getJSONObject(position));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(MMLocationManager.isGPSEnabled()) {
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
		String favorites = userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_BOOKMARKS, MMAPIConstants.DEFAULT_STRING);
		if(!favorites.equals(MMAPIConstants.DEFAULT_STRING)) {
			favoritesList = new JSONArray(favorites);
		} else {
			favoritesList = new JSONArray();
		}
		getFavorites();
		ArrayAdapter<MMResultsLocation> arrayAdapter = new MMSearchResultsArrayAdapter(getActivity(), R.layout.search_result_list_row, favoriteLocations);
		lvFavorites.setAdapter(arrayAdapter);
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void getFavorites() throws JSONException {
		favoriteLocations = new MMResultsLocation[favoritesList.length()];
		for(int i = 0; i < favoritesList.length(); i++) {
			JSONObject jObj = favoritesList.getJSONObject(i);
			favoriteLocations[i] = new MMResultsLocation();
			favoriteLocations[i].setLocName(jObj.getString(MMAPIConstants.JSON_KEY_NAME));
			favoriteLocations[i].setLocDist(MMUtility.calcDist(location, jObj.getDouble(MMAPIConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMAPIConstants.JSON_KEY_LONGITUDE)) + getString(R.string.miles));
			favoriteLocations[i].setLocAddr(jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS) + MMAPIConstants.DEFAULT_NEWLINE + jObj.getString(MMAPIConstants.JSON_KEY_LOCALITY) + MMAPIConstants.COMMA_SPACE + 
									jObj.getString(MMAPIConstants.JSON_KEY_REGION) + MMAPIConstants.COMMA_SPACE + jObj.getString(MMAPIConstants.JSON_KEY_POSTCODE));
		}
		
		// reverse array
		List temp = Arrays.asList(favoriteLocations);
		Collections.reverse(temp);
		favoriteLocations = (MMResultsLocation[]) temp.toArray();
		
		temp = new ArrayList<JSONObject>();
		for(int i = 0; i < favoritesList.length(); i++) {
			temp.add(favoritesList.get(i));
		}
		Collections.reverse(temp);
		favoritesList = new JSONArray(temp);
	}
	
	public interface OnMapIconClickListener {
		public void onMapIconClicked(int which);
	}
	
	public interface OnMMLocationSelectListener {
		public void onLocationSelect(Object obj);
	}
}
