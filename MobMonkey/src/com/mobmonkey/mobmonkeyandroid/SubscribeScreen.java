package com.mobmonkey.mobmonkeyandroid;

import com.mobmonkey.mobmonkeyandroid.R;

import android.app.Activity;
import android.os.Bundle;

public class SubscribeScreen extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_hold);
		setContentView(R.layout.subscribe_screen);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_bottom_out);
	}
}
