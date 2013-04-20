package com.mobmonkey.mobmonkeyandroid.utils;

import com.mobmonkey.mobmonkeyandroid.listeners.MMOnFragmentFinishListener;

import android.support.v4.app.Fragment;

/**
 * @author Dezapp, LLC
 *
 */
public abstract class MMFragment extends Fragment {
	protected MMOnFragmentFinishListener onFragmentFinishListener;
	
	public abstract void onFragmentBackPressed();
}
