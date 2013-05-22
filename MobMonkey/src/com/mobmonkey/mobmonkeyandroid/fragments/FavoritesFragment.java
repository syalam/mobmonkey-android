package com.mobmonkey.mobmonkeyandroid.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.AddLocationScreen;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMFavoritesArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.adapters.MMFavoritesAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;
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
	private SharedPreferences.Editor userPrefsEditor;
	
	private ImageButton ibMap;
	private Button btnAddLoc;
	private MMExpandedListView elvFavorites;
	
	private MMFavoritesArrayAdapter favoritesArrayAdapter;
	
	private MMOnMapIconFragmentClickListener mapIconClickListener;
	private MMOnSearchResultsFragmentItemClickListener locationSelectListener;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, TAG + "onCreateView");
		
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		View view = inflater.inflate(R.layout.fragment_favorites_screen, container, false);
		ibMap = (ImageButton) view.findViewById(R.id.ibmap);
		btnAddLoc = (Button) view.findViewById(R.id.btnaddloc);
		elvFavorites = (MMExpandedListView) view.findViewById(R.id.elvfavorites);

		ibMap.setOnClickListener(FavoritesFragment.this);
		btnAddLoc.setOnClickListener(FavoritesFragment.this);
		elvFavorites.setOnItemClickListener(FavoritesFragment.this);
		
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
				Log.d(TAG, TAG + "info: " + data.getStringExtra(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));
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
		if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
			switch(view.getId()) {
				case R.id.ibmap:
					mapIconClickListener.onMapIconFragmentClick(MMSDKConstants.FAVORITES_FRAGMENT_MAP);
					break;
				case R.id.btnaddloc:
					Intent intent = new Intent(getActivity(), AddLocationScreen.class);
					intent.putExtra(MMSDKConstants.REQUEST_CODE, MMSDKConstants.REQUEST_CODE_ADD_LOCATION);
					startActivityForResult(intent, MMSDKConstants.REQUEST_CODE_ADD_LOCATION);
					break;
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		locationSelectListener.onSearchResultsFragmentItemClick(favoritesArrayAdapter.getItem(position).toString());
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.d(TAG, TAG + "onResume");
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
		MMFavoritesAdapter.getFavorites(new FavoritesCallback(),
										MMConstants.PARTNER_ID,
										userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
										userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
		MMProgressDialog.displayDialog(getActivity(),
									   MMSDKConstants.DEFAULT_STRING_EMPTY,
									   getString(R.string.pd_updating_favorites));
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void getFavorites(String result) throws JSONException {
		JSONArray favorites = new JSONArray(result);
		userPrefsEditor.putString(MMSDKConstants.SHARED_PREFS_KEY_FAVORITES, result);
		userPrefsEditor.apply();
		
		if(favorites.length() > 0) {
			ArrayList<JSONObject> favoriteLocations = new ArrayList<JSONObject>();
			
			for(int i = 0; i < favorites.length(); i++) {
				favoriteLocations.add(favorites.getJSONObject(i));
			}

			favoritesArrayAdapter = new MMFavoritesArrayAdapter(getActivity(), R.layout.listview_row_favorites, favoriteLocations);
			elvFavorites.setAdapter(favoritesArrayAdapter);
			elvFavorites.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * Callback to update the user's favorites list in app data after making get favorites call to the server
	 * @author Dezapp, LLC
	 *
	 */
	private class FavoritesCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					Log.d(TAG, TAG + "response: " + ((String) obj));
					try {
						getFavorites((String) obj);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
	}
}
