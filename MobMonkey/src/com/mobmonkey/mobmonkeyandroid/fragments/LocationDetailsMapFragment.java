package com.mobmonkey.mobmonkeyandroid.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.Bundle;
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
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;

/**
 * @author Dezap, LLC
 *
 */
public class LocationDetailsMapFragment extends MMFragment {
	private static final String TAG = "LocationResultMapFragment: ";
	
	private SupportMapFragment smfLocation;
	private GoogleMap googleMap;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_locationdetails_map, container, false);
		smfLocation = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fraglocationdetailsmap);
		googleMap = smfLocation.getMap();
		
		JSONObject jObj;
		try {
			jObj = new JSONObject(getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));			
			
			Location location = MMLocationManager.getGPSLocation(new MMLocationListener());
			
			LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
			LatLng resultLocLatLng = new LatLng(jObj.getDouble(MMSDKConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE));
			
			googleMap.addMarker(new MarkerOptions().
				position(resultLocLatLng).
				title(jObj.getString(MMSDKConstants.JSON_KEY_NAME))
				.snippet(jObj.getString(MMSDKConstants.JSON_KEY_ADDRESS)));

			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 16));
			googleMap.setMyLocationEnabled(true);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		try {
			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
			transaction.remove(smfLocation);
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

}
