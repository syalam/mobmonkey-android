package com.mobmonkey.mobmonkey;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

/**
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

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
		return;
	}

}
