package com.mobmonkey.mobmonkeyandroid.utils;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class MMSupportMapFragment extends SupportMapFragment {
	private static final String TAG = "MMSupportMapFragment: ";
	
	public MMSupportMapFragment() {
		
	}
	
	public MMSupportMapFragment(GoogleMapOptions googleMapOptions) {
		MMSupportMapFragment mmSupportMapFragment = new MMSupportMapFragment();
		Bundle args = new Bundle();
		args.putParcelable(MMSDKConstants.GOOGLE_MAP_OPTIONS, googleMapOptions);
		mmSupportMapFragment.setArguments(args);
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.gms.maps.SupportMapFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup view, Bundle savedInstanceState) {
		Log.d(TAG, TAG + "onCreateView");
		View layout = super.onCreateView(inflater, view, savedInstanceState);
		FrameLayout frameLayout = new FrameLayout(getActivity());
		frameLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		((ViewGroup) layout).addView(frameLayout, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return layout;
	}

	/**
	 * 
	 * @return
	 */
	public static MMSupportMapFragment newInstance() {
		return new MMSupportMapFragment();
	}
	
	/**
	 * 
	 * @param googleMapOptions
	 * @return
	 */
	public static MMSupportMapFragment newInstance(GoogleMapOptions googleMapOptions) {
		MMSupportMapFragment mmSupportMapFragment = new MMSupportMapFragment();
		Bundle args = new Bundle();
		args.putParcelable(MMSDKConstants.GOOGLE_MAP_OPTIONS, googleMapOptions);
		mmSupportMapFragment.setArguments(args);
		return mmSupportMapFragment;
	}
}
