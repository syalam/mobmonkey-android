package com.mobmonkey.mobmonkey;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchScreen extends Activity {
	SharedPreferences userPrefs;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userPrefs = getSharedPreferences("USER_PREFS", MODE_PRIVATE);
		setContentView(R.layout.searchscreen);
		
		userPrefs.getString(MMAPIConstants.KEY_EMAIL_ADDRESS, MMAPIConstants.DEFAULT_STRING);
		userPrefs.getString(MMAPIConstants.KEY_PASSWORD, MMAPIConstants.DEFAULT_STRING);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
		return;
	}
}
