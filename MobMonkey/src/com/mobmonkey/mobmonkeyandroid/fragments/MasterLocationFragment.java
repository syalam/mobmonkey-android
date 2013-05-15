package com.mobmonkey.mobmonkeyandroid.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMNearbyLocationsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnMasterLocationNearbyLocationsFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class MasterLocationFragment extends MMFragment implements OnItemClickListener,
																  OnClickListener {
	private static final String TAG = "MasterLocationFragment: ";
	private JSONArray nearbyLocations;
	
	private MMExpandedListView elvNearbyLocations;
	private LinearLayout llLoadMore;
	
	private MMNearbyLocationsArrayAdapter nearbyLocationsArrayAdapter;
	
	private int nearbyLocationsCount = 5;
	
	private MMOnMasterLocationNearbyLocationsFragmentItemClickListener masterLocationNearbyLocationsItemClickListener;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_master_location_screen, container, false);
		elvNearbyLocations = (MMExpandedListView) view.findViewById(R.id.elvnearbylocations);		
		llLoadMore = (LinearLayout) view.findViewById(R.id.llloadmore);
		
		elvNearbyLocations.setOnItemClickListener(MasterLocationFragment.this);
		llLoadMore.setOnClickListener(MasterLocationFragment.this);
		
		try {
			if(!getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_NEARBY_LOCATIONS).equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
				nearbyLocations = new JSONArray(getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_NEARBY_LOCATIONS));
			} else {
				nearbyLocations = new JSONArray();
			}
			setNearbyLocations();
		} catch (JSONException e) {
			e.printStackTrace();
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
		if(activity instanceof MMOnMasterLocationNearbyLocationsFragmentItemClickListener) {
			masterLocationNearbyLocationsItemClickListener = (MMOnMasterLocationNearbyLocationsFragmentItemClickListener) activity;
		}
	}

	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		masterLocationNearbyLocationsItemClickListener.onMasterLocationNearbyLocationsItemClick(nearbyLocationsArrayAdapter.getItem(position));
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
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
	 * @see com.mobmonkey.mobmonkeyandroid.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		
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
		elvNearbyLocations.setAdapter(nearbyLocationsArrayAdapter);
	}
}
