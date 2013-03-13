package com.mobmonkey.mobmonkey;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
			
			Log.d(TAG, obj.toString());
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
				break;
			// near me
			case 3:
				break;
		}
	}
}
