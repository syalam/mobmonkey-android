package com.mobmonkey.mobmonkey;

import org.apache.http.HttpResponse;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

/**
 * Android {@link Activity} screen displays what's trending now for the user
 * @author Dezapp, LLC
 *
 */
public class TrendingNowScreen extends Activity {

	private static String TAG = "TrendingNowScreen";
	private SharedPreferences userPrefs;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trending_now_screen);
		
		init();
	}

	private void init() {
		
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		
		// dummy test
		/*
		MMTrendingAdapter.getTrending(new TrendingCallback(), 
									  "topviewed", 
									  "", 
									  true, 
									  true, 
									  300, 
									  300, 
									  250, 
									  true, 
									  "300", 
									  true,
									  MMConstants.PARTNER_ID, 
									  userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
									  userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
									  */
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
			
			Log.d(TAG, obj.toString());
		}
		
	}
}
