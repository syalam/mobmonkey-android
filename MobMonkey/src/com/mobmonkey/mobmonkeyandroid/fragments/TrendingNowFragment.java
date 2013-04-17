package com.mobmonkey.mobmonkeyandroid.fragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMTrendingArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMTrendingItem;
import com.mobmonkey.mobmonkeysdk.adapters.MMTrendingAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
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
	
	private String categoryIds;
	
	private ListView lvTrending;
	
	private OnTrendingItemClickListener listener;
	
	/*
	 * 	(non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Context.MODE_PRIVATE);
		
		View view = inflater.inflate(R.layout.fragment_trendingnow_screen, container, false);
		lvTrending = (ListView) view.findViewById(R.id.lvtrending);
		
		createTrending();		
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnTrendingItemClickListener) {
			listener = (OnTrendingItemClickListener) activity;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
//		MMProgressDialog.displayDialog(getActivity(), MMAPIConstants.DEFAULT_STRING_EMPTY, getString(R.string.pd_loading) + 
//				MMAPIConstants.DEFAULT_STRING_SPACE + ((TextView) view.findViewById(R.id.tvtrending)).getText().toString() + 
//				getString(R.string.pd_ellipses));
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
				listener.onTrendingItemClick(position);
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
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		getTrendingCount();
		super.onResume();
	}

	/**
	 * 
	 */
	public void createTrending() {
		MMTrendingItem[] data = new MMTrendingItem[getResources().getStringArray(R.array.trending_category).length];
		for(int i = 0; i < data.length; i++) {
			data[i] = new MMTrendingItem();
			data[i].title = getResources().getStringArray(R.array.trending_category)[i];
			data[i].counter = MMAPIConstants.DEFAULT_INT_ZERO;
		}
		
		MMTrendingArrayAdapter arrayAdapter = new MMTrendingArrayAdapter(getActivity(), R.layout.trending_list_row, data);
		lvTrending.setAdapter(arrayAdapter);
		lvTrending.setOnItemClickListener(TrendingNowFragment.this);
		
		try {
			JSONObject categories = new JSONObject(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, MMAPIConstants.DEFAULT_STRING_EMPTY));
			findTopTenCategoryIds(categories.toJSONArray(categories.names()));
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws JSONException
	 */
	private void findTopTenCategoryIds(JSONArray categories) throws JSONException {
		categoryIds = MMAPIConstants.DEFAULT_STRING_EMPTY;
		
		JSONArray topTenCategories = new JSONArray();
		
		FindTopTen:
		for(int i = 0; i < categories.length(); i++) {
			JSONArray jArr = categories.getJSONArray(i);
			for(int j = 0; j < jArr.length(); j++) {
				if(jArr.getJSONObject(j).getString(MMAPIConstants.JSON_KEY_PARENTS).compareTo("432") == 0) {
					topTenCategories.put(categories.getJSONArray(i));
				}
				if(topTenCategories.length() == 10) {
					break FindTopTen;
				}
			}
		}
		
		for(int i = 0; i < topTenCategories.length(); i++) {
			for(int j = 0; j < topTenCategories.getJSONArray(i).length(); j++) {
				categoryIds += topTenCategories.getJSONArray(i).getJSONObject(j).getString(MMAPIConstants.JSON_KEY_CATEGORY_ID) + ",";
			}
		}
	}
	
	/**
	 * 
	 */
	private void getTrendingCount() {
		if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
			MMTrendingAdapter.getTrendingCounts(new TrendingCountsCallback(),
											   MMAPIConstants.SEARCH_TIME_DAY,
											   userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING_EMPTY),
											   userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING_EMPTY),
											   MMConstants.PARTNER_ID);
		} else {
			MMProgressDialog.dismissDialog();
		}
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	public interface OnTrendingItemClickListener {
		public void onTrendingItemClick(int position);
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class TrendingCountsCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			Log.d(TAG, TAG + "Trending: " + ((String) obj));
			
			if(obj != null) {
				MMProgressDialog.dismissDialog();
				try {
					JSONObject jObj = new JSONObject((String)obj);
					MMTrendingItem[] data = new MMTrendingItem[getResources().getStringArray(R.array.trending_category).length];
					for(int i = 0; i < data.length; i++) {
						MMTrendingItem item = new MMTrendingItem();
						item.title = getResources().getStringArray(R.array.trending_category)[i];
						
						if(i == 0) {
							item.counter = jObj.getInt(MMAPIConstants.JSON_KEY_BOOKMARK_COUNT);
						} else if(i == 1) {
							item.counter = jObj.getInt(MMAPIConstants.JSON_KEY_INTEREST_COUNT);
						} else if(i == 2) {
							item.counter = jObj.getInt(MMAPIConstants.JSON_KEY_TOP_VIEWED_COUNT);
						} else if(i == 3) {
							item.counter = jObj.getInt(MMAPIConstants.JSON_KEY_NEARBY_COUNT);
						}
						
						data[i] = item;
					}
					
					MMTrendingArrayAdapter arrayAdapter = new MMTrendingArrayAdapter(getActivity(), R.layout.trending_list_row, data);
					lvTrending.setAdapter(arrayAdapter);
					lvTrending.setOnItemClickListener(TrendingNowFragment.this);
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}