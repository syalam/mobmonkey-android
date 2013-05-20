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
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMExistingHotSpotsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMNearbyLocationsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnExistingHotSpotsFragmentCreateHotSpotClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnMasterLocationNearbyLocationsFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnNearbyLocationsItemClickListener;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.adapters.MMMakeARequestAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class ExistingHotSpotsFragment extends MMFragment implements OnItemClickListener,
																	 OnClickListener {
	private static final String TAG = "ExistingHotSpotsFragment: ";
	private JSONObject parentLocation;
	private JSONArray subLocations;
	
	private MMExpandedListView elvExistingHotSpots;
	private Button btnCreateHotSpot;
	
	private MMExistingHotSpotsArrayAdapter existingHotSpotsArrayAdapter;
	
	private MMOnNearbyLocationsItemClickListener nearbyLocationsItemClickListener;
	private MMOnExistingHotSpotsFragmentCreateHotSpotClickListener existingHotSpotsCreateHotSpotClickListener;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_existing_hot_spots, container, false);
		elvExistingHotSpots = (MMExpandedListView) view.findViewById(R.id.elvexistinghotspots);
		btnCreateHotSpot = (Button) view.findViewById(R.id.btncreatehotspot);
		
		elvExistingHotSpots.setOnItemClickListener(ExistingHotSpotsFragment.this);
		btnCreateHotSpot.setOnClickListener(ExistingHotSpotsFragment.this);
		Log.d(TAG, TAG + "fragmentManager: " + getFragmentManager().findFragmentByTag(MMSDKConstants.MMSUPPORT_MAP_FRAGMENT_TAG));
		try {
			parentLocation = new JSONObject(getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_EXISTING_HOT_SPOTS));
			subLocations = parentLocation.getJSONArray(MMSDKConstants.JSON_KEY_SUB_LOCATIONS);
			setExistingHotSpots();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return view;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MMOnNearbyLocationsItemClickListener) {
			nearbyLocationsItemClickListener = (MMOnNearbyLocationsItemClickListener) activity;
			if(activity instanceof MMOnExistingHotSpotsFragmentCreateHotSpotClickListener) {
				existingHotSpotsCreateHotSpotClickListener = (MMOnExistingHotSpotsFragmentCreateHotSpotClickListener) activity;
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		nearbyLocationsItemClickListener.onNearbyLocationsItemClick(existingHotSpotsArrayAdapter.getItem(position));
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btncreatehotspot:
				existingHotSpotsCreateHotSpotClickListener.onExistingHotSpotsCreateHotSpotClick(parentLocation, MMSDKConstants.REQUEST_CODE_EXISTING_HOT_SPOTS);
				break;
		}
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		
	}
	
	private void setExistingHotSpots() throws JSONException {
		ArrayList<JSONObject> existingHotSpots = new ArrayList<JSONObject>();
		for(int i = 0; i < subLocations.length(); i++) {
			existingHotSpots.add(subLocations.getJSONObject(i));
		}
		
		existingHotSpotsArrayAdapter = new MMExistingHotSpotsArrayAdapter(getActivity(), R.layout.listview_row_existing_hot_spots, existingHotSpots);
		elvExistingHotSpots.setAdapter(existingHotSpotsArrayAdapter);
	}
}
