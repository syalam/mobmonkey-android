package com.mobmonkey.mobmonkey.fragments;

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.R.layout;
import com.mobmonkey.mobmonkey.utils.MMFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Dezapp, LLC
 *
 */
public class MyInterestsFragment extends MMFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_myinterests_screen, container, false);
		
		return view;
	}

	@Override
	public void onFragmentBackPressed() {
		// TODO Auto-generated method stub
		
	}

}