package com.mobmonkey.mobmonkey;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

/**
 * Android {@link Activity} screen displays what's trending now for the user
 * @author Dezapp, LLC
 *
 */
public class TrendingNowScreen extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trending_now_screen);
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

}
