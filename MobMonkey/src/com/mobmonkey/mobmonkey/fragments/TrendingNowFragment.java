package com.mobmonkey.mobmonkey.fragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.mobmonkey.mobmonkey.MainScreen;
import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkey.utils.MMTrendingArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMTrendingItem;
import com.mobmonkey.mobmonkeyapi.adapters.MMTrendingAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationListener;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationManager;

/**
 * @author Dezapp, LLC
 *
 */
public class TrendingNowFragment extends MMFragment implements OnItemClickListener {
	private static final String TAG = "TrendingNowFragment: ";
	
	private SharedPreferences userPrefs;
	private JSONArray categories;
	
	private ListView lvTrending;
	
	OnTrendingItemClickListener listener;
	
	/*
	 * 	(non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Context.MODE_PRIVATE);
		
		View view = inflater.inflate(R.layout.fragment_trendingnow_screen, container, false);
		
		lvTrending = (ListView) view.findViewById(R.id.lvtrending);
		
		MMTrendingItem[] data = new MMTrendingItem[getResources().getStringArray(R.array.trending_category).length];
		for(int i = 0; i < data.length; i++) {
			data[i] = new MMTrendingItem();
			data[i].title = getResources().getStringArray(R.array.trending_category)[i];
			data[i].counter = "0";
		}
		
		MMTrendingArrayAdapter arrayAdapter = new MMTrendingArrayAdapter(getActivity(), R.layout.trending_list_row, data);
		lvTrending.setAdapter(arrayAdapter);
		lvTrending.setOnItemClickListener(TrendingNowFragment.this);
		lvTrending.setEnabled(false);
		
		try {
			JSONObject cats = new JSONObject(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, MMAPIConstants.DEFAULT_STRING));
			categories = cats.toJSONArray(cats.names());
					//new JSONArray(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, 
					  //MMAPIConstants.DEFAULT_STRING));
			
			trending(findTopTenCategories());
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		
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
		switch (position) {
			// bookmarks
			case 0:
				break;
			// my interests
			case 1:
				break;
			// top viewed
			case 2:
				MMTrendingAdapter.getTrending(new TrendingCallback(position), 
									 		  "topviewed", 
											  "week", 
											  false, 
											  false, 
											  0.0d, 
											  0.0d, 
											  0, 
											  false, 
											  "", 
											  false, 
											  MMConstants.PARTNER_ID, 
											  userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
											  userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
	
				break;
			// near me
			case 3:
				break;
		}
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		
	}
	
	private String findTopTenCategories() throws JSONException {
		String categoryIds = MMAPIConstants.DEFAULT_STRING;
		
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
				categoryIds += topTenCategories.getJSONArray(i).getJSONObject(j).getString(MMAPIConstants.JSON_KEY_CATEGORY_ID)+",";
			}
		}
		
		return categoryIds;
	}
	
	private void trending(String categoryIds) {
		if(MMLocationManager.isGPSEnabled()) {
			Location location = MMLocationManager.getGPSLocation(new MMLocationListener());
			
			MMTrendingAdapter.getTrending(new CountOnlyCallback(), 
									      MMAPIConstants.URL_TOPVIEWED, 
									      MMAPIConstants.SEARCH_TIME_DAY, 
									      true,
									      true,
									      location.getLatitude(), 
									      location.getLongitude(), 
									      MMAPIConstants.SEARCH_RADIUS_FIVE_MILE, 
									      true, 
									      categoryIds, 
									      true, 
									      MMConstants.PARTNER_ID, 
										  userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
										  userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
		} else {
			MainScreen.closeProgressDialog();
		}
	}
	
	public interface OnTrendingItemClickListener {
		public void onTrendingItemClick(int position, String trends);
	}
	
	private class TrendingCallback implements MMCallback {
		private int position;
		
		public TrendingCallback(int position) {
			this.position = position;
		}
		
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				listener.onTrendingItemClick(position, ((String) obj));
			}
		}
	}
	
	private class CountOnlyCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			Log.d(TAG, TAG + "Trending: " + ((String) obj));
			
			if(obj != null) {
				MainScreen.closeProgressDialog();
				try {
					JSONObject jObj = new JSONObject((String)obj);
					Log.d(TAG, TAG + jObj.getString("bookmarkCount"));
					MMTrendingItem[] data = new MMTrendingItem[getResources().getStringArray(R.array.trending_category).length];
					for(int i = 0; i < data.length; i++) {
						MMTrendingItem item = new MMTrendingItem();
						item.title = getResources().getStringArray(R.array.trending_category)[i];
						
						if(item.title.equalsIgnoreCase("bookmarks")) {
							item.counter = jObj.getString(MMAPIConstants.JSON_KEY_BOOKMARK_COUNT);
						} else if(item.title.equalsIgnoreCase("my interests")) {
							item.counter = jObj.getString(MMAPIConstants.JSON_KEY_INTEREST_COUNT);
						} else if(item.title.equalsIgnoreCase("top viewed")) {
							item.counter = jObj.getString(MMAPIConstants.JSON_KEY_TOP_VIEWED_COUNT);
						} else if(item.title.equalsIgnoreCase("near me")) {
							item.counter = jObj.getString(MMAPIConstants.JSON_KEY_NEARBY_COUNT);
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