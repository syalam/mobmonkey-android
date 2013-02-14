package com.mobmonkey.mobmonkey;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

/**
 * @author Dezapp, LLC
 *
 */
public class SettingsScreen extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingsscreen);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
		return;
	}

	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btnsignout:
				// TODO: Logout from what ever login
				finish();
				break;
		}
	}
}
