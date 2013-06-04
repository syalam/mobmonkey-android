package com.mobmonkey.mobmonkeyandroid.utils;

import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnFragmentFinishListener;

public abstract class MMListFragment extends SherlockListFragment {
	protected MMOnFragmentFinishListener fragmentFinishListener;

	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public abstract void onListItemClick(ListView l, View v, int position, long id);
	
	public abstract void onFragmentBackPressed();



	
}
