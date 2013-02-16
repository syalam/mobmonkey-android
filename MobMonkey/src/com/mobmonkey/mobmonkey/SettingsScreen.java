package com.mobmonkey.mobmonkey;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkeyapi.adapters.MMSignOutAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

/**
 * @author Dezapp, LLC
 *
 */
public class SettingsScreen extends Activity {
	private static final String TAG = "SettingsScreen: ";
	
	SharedPreferences userPrefs;
	SharedPreferences.Editor userPrefsEditor;
	
	ProgressDialog progressDialog;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_screen);
		
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
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
				MMSignOutAdapter.signOut(new SignOutCallback(), userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
						userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), MMConstants.PARTNER_ID);
				progressDialog = ProgressDialog.show(SettingsScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_signing_out), true, false);
				break;
		}
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class SignOutCallback implements MMCallback {
		public void processCallback(Object obj) {
			if(progressDialog != null) {
				progressDialog.dismiss();
			}
			
			try {
				JSONObject response = new JSONObject((String) obj);
				if(response.getString(MMAPIConstants.KEY_RESPONSE_STATUS).equals(MMAPIConstants.RESPONSE_STATUS_SUCCESS)) {
					// TODO: clear all the saved username/email/passwords/tokens
					Session session = Session.getActiveSession();
					if(session != null) {
						Log.d(TAG, TAG + "session not null");
						session.closeAndClearTokenInformation();
					}
					Toast.makeText(SettingsScreen.this, R.string.toast_sign_out_successful, Toast.LENGTH_SHORT).show();
	 				finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			Log.d(TAG, TAG + "callback response: " + (String) obj);
		}
	}
}
