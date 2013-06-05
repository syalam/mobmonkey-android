package com.mobmonkey.mobmonkeyandroid;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.mobmonkey.mobmonkeysdk.adapters.MMUserAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

public class SubscribeScreen extends SherlockActivity {
	SharedPreferences userPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			this.setTheme(com.actionbarsherlock.R.style.Theme_Sherlock);
		}
		setContentView(R.layout.subscribe_screen);
		overridePendingTransition(0, 0);
		getSupportActionBar().setTitle(getResources().getString(R.string.tv_title_subscribe));
		
		super.onCreate(savedInstanceState);
		// getWindow().setWindowAnimations(0);
		 overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_hold);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.navigation_bar));
		userPrefs = getSharedPreferences(MMSDKConstants.USER_PREFS,
				MODE_PRIVATE);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.actionbarsherlock.app.SherlockActivity#onOptionsItemSelected(com.
	 * actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
        // app icon in action bar clicked; go home
        Intent intent = new Intent(this, MainScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
		overridePendingTransition(R.anim.slide_hold,
				R.anim.slide_bottom_out);
        return true;
    default:
        return super.onOptionsItemSelected(item);
	}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_bottom_out);
	}

	public void viewOnClick(View view) {
		switch (view.getId()) {
		case R.id.btnsubscribe:
			MMUserAdapter.subscribeUser(new SubscribeCallback());
			break;
		}
	}

	/**
	 * 
	 * @author Dezapp, LLC
	 * 
	 */
	private class SubscribeCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if (obj != null) {
				if (((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(SubscribeScreen.this,
							getString(R.string.toast_connection_timed_out),
							Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObj = new JSONObject((String) obj);
						Toast.makeText(
								SubscribeScreen.this,
								jObj.getString(MMSDKConstants.JSON_KEY_DESCRIPTION),
								Toast.LENGTH_SHORT).show();
						finish();
						overridePendingTransition(R.anim.slide_hold,
								R.anim.slide_bottom_out);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	protected Context getActivityContext() {
		return this;
	}
}
