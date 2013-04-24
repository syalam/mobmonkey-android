package com.mobmonkey.mobmonkeyandroid.fragments;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Dezapp, LLC
 *
 */
public class AddNotificationsFragment extends MMFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_add_notifications, container, false);
		
		
		return view;
	}

	@Override
	public void onFragmentBackPressed() {
		
	}
}
