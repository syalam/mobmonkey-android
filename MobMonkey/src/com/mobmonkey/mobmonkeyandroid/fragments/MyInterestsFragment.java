package com.mobmonkey.mobmonkeyandroid.fragments;

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.R.layout;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;

import android.app.Activity;
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnFragmentFinishListener) {
			onFragmentFinishListener = (OnFragmentFinishListener) activity;
		}
	}

	@Override
	public void onFragmentBackPressed() {
		onFragmentFinishListener.onFragmentFinish();
	}

}
