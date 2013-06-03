package com.mobmonkey.mobmonkeyandroid.utils;

import com.actionbarsherlock.app.SherlockFragment;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnFragmentFinishListener;

import android.support.v4.app.Fragment;

/**
 * @author Dezapp, LLC
 *
 */
public abstract class MMFragment extends SherlockFragment {
	protected MMOnFragmentFinishListener fragmentFinishListener;
	
	public abstract void onFragmentBackPressed();
}
