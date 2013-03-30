package com.mobmonkey.mobmonkey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMTrendingArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMTrendingItem;
import com.mobmonkey.mobmonkeyapi.adapters.MMTrendingAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationListener;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationManager;

/**
 * Android {@link Activity} screen displays what's trending now for the user
 * @author Dezapp, LLC
 *
 */
public class TrendingNowScreen extends Activity implements OnItemClickListener {
	private static String TAG = "TrendingNowScreen: ";
	
	private JSONArray categories;
	
	private SharedPreferences userPrefs;
	private ListView lvTrending;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trending_now_screen);
		init();
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		Log.d(TAG, TAG + "position clicked: " + position);
		switch (position) {
			// bookmarks
			case 0:
				break;
			// my interests
			case 1:
				break;
			// top viewed
			case 2:
				
//				MMTrendingAdapter.getTrending(new TrendingCallback(), 
//									 		  "topviewed", 
//											  "week", 
//											  false, 
//											  false, 
//											  0.0d, 
//											  0.0d, 
//											  0, 
//											  false, 
//											  "", 
//											  false, 
//											  MMConstants.PARTNER_ID, 
//											  userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
//											  userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));

				break;
			// near me
			case 3:
				break;
		}
	}
	
	/**
	 * Handler when back button is pressed, it will not close and destroy the current {@link Activity} but instead it will remain on the current {@link Activity}
	 */
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
		return;
	}
	
	private void init() {
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		
		lvTrending = (ListView) findViewById(R.id.lvtrending);
		
		MMTrendingItem[] data = new MMTrendingItem[getResources().getStringArray(R.array.trending_category).length];
		for(int i = 0; i < data.length; i++) {
			data[i] = new MMTrendingItem();
			data[i].title = getResources().getStringArray(R.array.trending_category)[i];
			data[i].counter = "0";
		}
		
		MMTrendingArrayAdapter arrayAdapter = new MMTrendingArrayAdapter(TrendingNowScreen.this, R.layout.trending_list_row, data);
		lvTrending.setAdapter(arrayAdapter);
		lvTrending.setOnItemClickListener(TrendingNowScreen.this);
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
	
	public interface OnTrendingNowLoadFinishListener {
		public void onLoadFinish();
	}
	
	private class TrendingCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {

			Intent intent = new Intent(TrendingNowScreen.this, TopViewedScreen.class);
			intent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_TRENDING_TOP_VIEWED, (String) obj);
			startActivity(intent);
		}
	}
	
	private class CountOnlyCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			Log.d(TAG, TAG + "Trending: " + ((String) obj));
			
			if(obj != null) {
//				MainScreen.closeProgressDialog();
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
					
					MMTrendingArrayAdapter arrayAdapter = new MMTrendingArrayAdapter(TrendingNowScreen.this, R.layout.trending_list_row, data);
					lvTrending.setAdapter(arrayAdapter);
					lvTrending.setOnItemClickListener(TrendingNowScreen.this);
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			}
		}
		
	}
}
