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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Android {@link Activity} screen displays the user settings
 * @author Dezapp, LLC
 *
 */
public class SettingsScreen extends Activity {
	private static final String TAG = "SettingsScreen: ";
	
	SharedPreferences userPrefs;
	SharedPreferences.Editor userPrefsEditor;
	
	ProgressDialog progressDialog;
	ListView lvSettingsCategory;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_screen);
		
		init();
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

	/**
	 * Handler for when {@link Button}s or any other {@link View}s are clicked.
	 * @param view {@link View} that is clicked
	 */
	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btnsignout:
				MMSignOutAdapter.signOut(new SignOutCallback(), userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
						userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), MMConstants.PARTNER_ID);
				progressDialog = ProgressDialog.show(getParent(), MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_signing_out), true, false);
				break;
		}
	}
	
	/**
	 * Initialize all the variables to be used in {@link SettingsScreen}
	 */
	private void init() { 
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		lvSettingsCategory = (ListView) findViewById(R.id.lvsettingscategory);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SettingsScreen.this, R.layout.settings_category_list_row, R.id.tvsettingscategory, getResources().getStringArray(R.array.settings_category));
		lvSettingsCategory.setAdapter(arrayAdapter);
		
		lvSettingsCategory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Intent intent;
				View v;
				
				switch(position) {
					// My Info
					case 0:
						intent = new Intent(SettingsScreen.this, MyInfoScreen.class);
						v = SettingsGroup.settingsGroup.getLocalActivityManager().startActivity("My Info", intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
						
						SettingsGroup.settingsGroup.replaceView(v);
//						startActivity();
						break;
					// Social Networks
					case 1:
						intent = new Intent(SettingsScreen.this, SocialNetworksScreen.class);
						v = SettingsGroup.settingsGroup.getLocalActivityManager().startActivity("Social Networks", intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
						
						SettingsGroup.settingsGroup.replaceView(v);
//						startActivity(new Intent(SettingsScreen.this, SocialNetworksScreen.class));
						break;
					// My Interests
					case 2:
						intent = new Intent(SettingsScreen.this, MyInterestsScreen.class);
						v = SettingsGroup.settingsGroup.getLocalActivityManager().startActivity("My Interests", intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
						
						SettingsGroup.settingsGroup.replaceView(v);
//						startActivity();
						break;
					// Subscribe
					case 3:
						intent = new Intent(SettingsScreen.this, SubscribeScreen.class);
						startActivity(intent);
						break;
				}
			}
		});
	}
	
    /**
     * Custom {@link MMCallback} specifically for {@link SettingsScreen} to be processed after receiving response from MobMonkey server.
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
				}
 				finish();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			Log.d(TAG, TAG + "callback response: " + (String) obj);
		}
	}
}
