package com.mobmonkey.mobmonkeyandroid.listeners;

import org.json.JSONObject;

import android.view.View;
import android.view.View.OnClickListener;

public class MMLocationNameOnClickListener implements OnClickListener {
	
	private OnLocationNameClickFragmentListener locationNameClickListener;
	private JSONObject locationInfo;
	
	public MMLocationNameOnClickListener(OnLocationNameClickFragmentListener locationNameClickListener, 
										 JSONObject locationDetails) {
		this.locationNameClickListener = locationNameClickListener;
		this.locationInfo = locationDetails;
	}
	
	@Override
	public void onClick(View v) {
		locationNameClickListener.locationNameClick(locationInfo);
	}
}
