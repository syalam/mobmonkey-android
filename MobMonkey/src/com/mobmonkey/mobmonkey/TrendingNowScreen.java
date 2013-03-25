package com.mobmonkey.mobmonkey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
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

/**
 * Android {@link Activity} screen displays what's trending now for the user
 * @author Dezapp, LLC
 *
 */
public class TrendingNowScreen extends Activity implements OnItemClickListener{

	private static String TAG = "TrendingNowScreen";
	private SharedPreferences userPrefs;
	private ListView lvTrending;
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trending_now_screen);
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		init();
	}

	private void init() {
		
		lvTrending = (ListView) findViewById(R.id.lvtrending);
		
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		double longitude = location.getLongitude();
		double latitude = location.getLatitude();
		
		try {
			JSONArray categories = new JSONArray(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, 
					  MMAPIConstants.DEFAULT_STRING));
			String categoryIds = "";
			
			JSONArray topTenCategories = new JSONArray();
			
			FindTopTen:
			for(int i = 0; i < categories.length(); i++) {
				if(categories.getJSONObject(i).getString("parents").compareTo("1") == 0) {
					topTenCategories.put(categories.getJSONObject(i));
				}
				if(topTenCategories.length() == 10) {
					break FindTopTen;
				}
			}
			
			for(int i = 0; i < topTenCategories.length(); i++) {
				categoryIds += topTenCategories.getJSONObject(i).getString("categoryId")+",";
			}
			categoryIds.substring(0, categoryIds.length()-1);
			
			MMTrendingAdapter.getTrending(new CountOnlyCallback(), 
									      MMAPIConstants.URL_TOPVIEWED, 
									      MMAPIConstants.SEARCH_TIME_DAY, 
									      true, 
									      true, 
									      latitude, 
									      longitude, 
									      MMAPIConstants.SEARCH_RADIUS_FIVE_MILE, 
									      true, 
									      categoryIds, 
									      true, 
									      MMConstants.PARTNER_ID, 
										  userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
										  userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
			
		} catch (JSONException ex) {
			ex.printStackTrace();
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
			try {
				JSONObject jObj = new JSONObject((String)obj);
				Log.d(TAG, jObj.getString("bookmarkCount"));
				MMTrendingItem[] data = new MMTrendingItem[getResources().getStringArray(R.array.trending_category).length];
				for(int i = 0; i < data.length; i++) {
					MMTrendingItem item = new MMTrendingItem();
					item.title = getResources().getStringArray(R.array.trending_category)[i];
					
					if(item.title.equalsIgnoreCase("bookmarks")) {
						item.counter = jObj.getString("bookmarkCount");
					} else if(item.title.equalsIgnoreCase("my interests")) {
						item.counter = jObj.getString("interestCount");
					} else if(item.title.equalsIgnoreCase("top viewed")) {
						item.counter = jObj.getString("nearbyCount");
					} else if(item.title.equalsIgnoreCase("near me")) {
						item.counter = jObj.getString("topviewedCount");
					}
					
					data[i] = item;
				}
				
				MMTrendingArrayAdapter arrayAdapter 
					= new MMTrendingArrayAdapter(TrendingNowScreen.this, R.layout.trending_list_row, data);
				lvTrending.setAdapter(arrayAdapter);
				
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		switch (position) {
			// bookmarks
			case 0:
				break;
			// my interests
			case 1:
				break;
			// top viewed
			case 2:
				
				MMTrendingAdapter.getTrending(new TrendingCallback(), 
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
}
