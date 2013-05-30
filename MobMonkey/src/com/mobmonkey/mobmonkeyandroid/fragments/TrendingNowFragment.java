package com.mobmonkey.mobmonkeyandroid.fragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.mobmonkey.mobmonkeyandroid.MainScreen;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMTrendingArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMTrendingItem;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.adapters.MMTrendingAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;

/**
 * @author Dezapp, LLC
 *
 */
public class TrendingNowFragment extends MMFragment implements OnItemClickListener {
	private static final String TAG = "TrendingNowFragment: ";
	
	private SharedPreferences userPrefs;
	
	private MMTrendingItem[] mmTrendingItem;
	
	private ListView lvTrending;
	private MMTrendingArrayAdapter arrayAdapter;
	
	private MMOnTrendingFragmentItemClickListener listener;
	
	/*
	 * 	(non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		
		View view = inflater.inflate(R.layout.fragment_trendingnow_screen, container, false);
		lvTrending = (ListView) view.findViewById(R.id.lvtrending);
		
		createTrending();		
		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MMOnTrendingFragmentItemClickListener) {
			listener = (MMOnTrendingFragmentItemClickListener) activity;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation() != null) {
			getTrendingCounts();
		} else {
			MMProgressDialog.dismissDialog();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		switch (position) {
			// bookmarks
			case 0:
				// TODO: to be removed when this is implemented
				MMProgressDialog.dismissDialog();
				break;
			// my interests
			case 1:
				// TODO: to be removed when this is implemented
				MMProgressDialog.dismissDialog();
				break;
			// top viewed
			case 2:
				listener.onTrendingFragmentItemClick(position);
				break;
			// near me
			case 3:
				// TODO: to be removed when this is implemented
				MMProgressDialog.dismissDialog();
				break;
		}
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		
	}

	/**
	 * 
	 */
	public void createTrending() {
		mmTrendingItem = new MMTrendingItem[getResources().getStringArray(R.array.trending_category).length];
		for(int i = 0; i < mmTrendingItem.length; i++) {
			mmTrendingItem[i] = new MMTrendingItem();
			mmTrendingItem[i].title = getResources().getStringArray(R.array.trending_category)[i];
			mmTrendingItem[i].counter = MMSDKConstants.DEFAULT_INT_ZERO;
		}
		
		arrayAdapter = new MMTrendingArrayAdapter(getActivity(), R.layout.listview_row_trending, mmTrendingItem);
		lvTrending.setAdapter(arrayAdapter);
		lvTrending.setOnItemClickListener(TrendingNowFragment.this);
	}
	
	/**
	 * 
	 */
	private void getTrendingCounts() {
		MMTrendingAdapter.getTrendingCounts(new TrendingCountsCallback(),
											MMSDKConstants.SEARCH_TIME_DAY);
	}
	
	/**
	 * 
	 * @param jObj
	 * @throws JSONException
	 */
	private void setTrendingCounts(JSONObject jObj) throws JSONException {
		int favoritesCount = jObj.getInt(MMSDKConstants.JSON_KEY_BOOKMARK_COUNT);
		int myInterestsCount = jObj.getInt(MMSDKConstants.JSON_KEY_INTEREST_COUNT);
		int topViewedCount = jObj.getInt(MMSDKConstants.JSON_KEY_TOP_VIEWED_COUNT);
		int nearMeCount = jObj.getInt(MMSDKConstants.JSON_KEY_NEARBY_COUNT);
		
		if(favoritesCount > 0) {
			mmTrendingItem[0].counter = favoritesCount;
		} else {
			mmTrendingItem[0].counter = MMSDKConstants.DEFAULT_INT_ZERO;
		}
		arrayAdapter.isEnabled(0);		
		
		if(myInterestsCount > 0) {
			mmTrendingItem[1].counter = myInterestsCount;
		} else {
			mmTrendingItem[1].counter = MMSDKConstants.DEFAULT_INT_ZERO;
		}
		arrayAdapter.isEnabled(1);
		
		if(topViewedCount > 0) {
			mmTrendingItem[2].counter = topViewedCount;
		} else {
			mmTrendingItem[2].counter = MMSDKConstants.DEFAULT_INT_ZERO;
		}
		arrayAdapter.isEnabled(2);
		
		if(nearMeCount > 0) {
			mmTrendingItem[3].counter = nearMeCount;
		} else {
			mmTrendingItem[3].counter = MMSDKConstants.DEFAULT_INT_ZERO;
		}
		arrayAdapter.isEnabled(3);
		
		arrayAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class TrendingCountsCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				Log.d(TAG, TAG + "Trending: " + ((String) obj));
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						setTrendingCounts(new JSONObject((String) obj));
					} catch (JSONException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}
}