package com.mobmonkey.mobmonkeyandroid.listeners;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.mobmonkey.mobmonkeyandroid.fragments.LocationDetailsFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeysdk.adapters.MMLocationDetailsAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

public class MMLocationNameOnClickListener implements OnClickListener{
	
	private OnLocationNameClickFragmentListener locationNameClickListener;
	private JSONObject locationDetails;
	
	public MMLocationNameOnClickListener(OnLocationNameClickFragmentListener locationNameClickListener, 
										 JSONObject locationDetails) {
		this.locationNameClickListener = locationNameClickListener;
		this.locationDetails = locationDetails;
	}
	
	@Override
	public void onClick(View v) {
		locationNameClickListener.locationNameClick(locationDetails);
	}

}
