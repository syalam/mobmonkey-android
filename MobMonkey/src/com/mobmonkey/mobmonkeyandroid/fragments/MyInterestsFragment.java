package com.mobmonkey.mobmonkeyandroid.fragments;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;

import android.app.Activity;
import android.os.Bundle;
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
		if(activity instanceof MMOnFragmentFinishListener) {
			onFragmentFinishListener = (MMOnFragmentFinishListener) activity;
		}
	}

	@Override
	public void onFragmentBackPressed() {
		onFragmentFinishListener.onFragmentFinish();
	}

}
