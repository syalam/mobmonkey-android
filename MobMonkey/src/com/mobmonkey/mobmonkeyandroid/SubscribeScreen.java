package com.mobmonkey.mobmonkeyandroid;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeysdk.adapters.MMUserAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SubscribeScreen extends Activity{
	SharedPreferences userPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_hold);
		
		userPrefs = getSharedPreferences(MMSDKConstants.USER_PREFS, MODE_PRIVATE);
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
	
	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btnsubscribe:
				MMUserAdapter.subscribeUser(new SubscribeCallback(),
											MMConstants.PARTNER_ID,
											userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
											userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
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
			if(obj != null) {
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(SubscribeScreen.this, getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObj = new JSONObject((String) obj);
						Toast.makeText(SubscribeScreen.this, jObj.getString(MMSDKConstants.JSON_KEY_DESCRIPTION), Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
