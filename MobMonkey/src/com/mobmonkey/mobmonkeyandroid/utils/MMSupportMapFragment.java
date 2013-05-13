package com.mobmonkey.mobmonkeyandroid.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * @author Dezapp, LLC
 *
 */
public class MMSupportMapFragment extends SupportMapFragment {

	/* (non-Javadoc)
	 * @see com.google.android.gms.maps.SupportMapFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup view, Bundle savedInstanceState) {
		View layout = super.onCreateView(inflater, view, savedInstanceState);
		FrameLayout frameLayout = new FrameLayout(getActivity());
		frameLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		((ViewGroup) layout).addView(frameLayout, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return layout;
	}
}
