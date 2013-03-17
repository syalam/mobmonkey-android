package com.mobmonkey.mobmonkey;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mobmonkey.mobmonkey.utils.MMConstants;
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
		
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		
		lvTrending = (ListView) findViewById(R.id.lvtrending);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TrendingNowScreen.this, R.layout.settings_category_list_row, R.id.tvsettingscategory, getResources().getStringArray(R.array.trending_category));
		
		lvTrending.setAdapter(arrayAdapter);
		lvTrending.setOnItemClickListener(this);
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
//			
//			Log.d(TAG, obj.toString());
			Intent intent = new Intent(TrendingNowScreen.this, TopViewedScreen.class);
			intent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_TRENDING_TOP_VIEWED, (String) obj);
			startActivity(intent);
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
