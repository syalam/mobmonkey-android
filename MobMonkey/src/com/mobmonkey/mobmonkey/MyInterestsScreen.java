package com.mobmonkey.mobmonkey;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MyInterestsScreen extends Activity {
	private static final String TAG = "MyInterestsScreen: ";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_interests_screen);
	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, TAG + "onBackPressed");
		
		SettingsGroup.settingsGroup.back();
		return;
	}
}
