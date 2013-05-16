package com.mobmonkey.mobmonkeyandroid.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMSupportMapFragment;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;

/**
 * @author Dezap, LLC
 *
 */
public class LocationDetailsMapFragment extends MMFragment {
	private static final String TAG = "LocationResultMapFragment: ";
	
	private FragmentManager fragmentManager;
	
	private MMSupportMapFragment smfLocation;
	private GoogleMap googleMap;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		fragmentManager = getFragmentManager();
		
		View view = inflater.inflate(R.layout.fragment_locationdetails_map, container, false);
		
		return view;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		getMMSupportMapFragment();
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		try {
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.remove(smfLocation);
			transaction.commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {

	}

	/**
	 * @throws JSONException 
	 * 
	 */
	private void getMMSupportMapFragment() {
		smfLocation = (MMSupportMapFragment) fragmentManager.findFragmentByTag(MMSDKConstants.MMSUPPORT_MAP_FRAGMENT_TAG);
		if(smfLocation == null) {
			smfLocation = new MMSupportMapFragment() {
				/* (non-Javadoc)
				 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
				 */
				@Override
				public void onActivityCreated(Bundle savedInstanceState) {
					super.onActivityCreated(savedInstanceState);
					googleMap = smfLocation.getMap();
					if(googleMap != null) {
						try {
							addToGoogleMap();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			};
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.add(R.id.lllocationdetailsmap, smfLocation, MMSDKConstants.MMSUPPORT_MAP_FRAGMENT_TAG);
			fragmentTransaction.commit();
		}
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void addToGoogleMap() throws JSONException {
		JSONObject jObj = new JSONObject(getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));			
		
		LatLng currentLoc = new LatLng(MMLocationManager.getLocationLatitude(), MMLocationManager.getLocationLongitude());
		LatLng resultLocLatLng = new LatLng(jObj.getDouble(MMSDKConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE));
		
		googleMap.addMarker(new MarkerOptions()
			.position(resultLocLatLng)
			.title(jObj.getString(MMSDKConstants.JSON_KEY_NAME))
			.snippet(jObj.getString(MMSDKConstants.JSON_KEY_ADDRESS)));

		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 16));
		googleMap.setMyLocationEnabled(true);
	}
}
