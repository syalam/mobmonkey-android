package com.mobmonkey.mobmonkeyandroid.utils;

import android.support.v4.app.Fragment;

/**
 * @author Dezapp, LLC
 *
 */
public abstract class MMFragment extends Fragment {
	protected OnFragmentFinishListener onFragmentFinishListener;
	
	public abstract void onFragmentBackPressed();
	
	public interface OnFragmentFinishListener {
		public void onFragmentFinish();
	}
}