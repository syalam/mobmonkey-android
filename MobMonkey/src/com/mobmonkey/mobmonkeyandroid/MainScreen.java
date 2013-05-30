package com.mobmonkey.mobmonkeyandroid;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeysdk.adapters.MMCategoryAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMCheckinAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMFavoritesAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMGCMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;

/**
 * Android {@link Activity} screen displays the signed in user portion of the application with different tabs.
 * @author Dezapp, LLC
 *
 */
public class MainScreen extends TabActivity {
	protected static final String TAG = "MainScreen: ";

	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	
	private TabWidget tabWidget;
	private TabHost tabHost;
	
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(MMSDKConstants.KEY_INTENT_EXTRA_MESSAGE);
            Toast.makeText(MainScreen.this, newMessage, Toast.LENGTH_LONG).show();
        }
    };
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, TAG + "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		checkForGPSLocation();
	}

//	/*
//	 * (non-Javadoc)
//	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
//	 */
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		Log.d(TAG, TAG + ":onActivityResult");
//		super.onActivityResult(requestCode, resultCode, data);
//		if(requestCode == MMSDKConstants.REQUEST_CODE_TURN_ON_GPS_LOCATION) {
//			if(MMLocationManager.isGPSEnabled()) {
//				checkForGPSAccess();
//			} else {
//				noGPSEnabled();
//			}
//		}
//	}

	/*
	 * (non-Javadoc)
	 * @see android.app.ActivityGroup#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		Log.d(TAG, TAG + "onDestroy");
		MMProgressDialog.dismissDialog();
		unregisterReceiver(mHandleMessageReceiver);
		super.onDestroy();
	}
	
	/**
	 * Function that check if user's device has GPS access. Display a {@link Toast} message informing the user if 
	 * there is no GPS access.
	 */
	private void checkForGPSLocation() {
		if(MMLocationManager.getGPSLocation() == null) {
			new AlertDialog.Builder(MainScreen.this)
	    	.setTitle(R.string.ad_title_no_location)
	    	.setMessage(R.string.ad_message_no_location)
	    	.setCancelable(false)
	    	.setNeutralButton(R.string.ad_btn_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					init();
				}
			})
	    	.show();
	    } else {
			init();
	    }
	}
	
	/**
	 * Initialize all the variables to be used in {@link MainScreen}
	 */
	private void init() {
		Log.d(TAG, "init");
		userPrefs = getSharedPreferences(MMSDKConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		if(userPrefs.getBoolean(MMSDKConstants.KEY_USE_OAUTH, false)) {
			MMAdapter.useOAuth(MMConstants.PARTNER_ID,
							   userPrefs.getString(MMSDKConstants.KEY_OAUTH_PROVIDER_USER_NAME, MMSDKConstants.DEFAULT_STRING_EMPTY),
							   userPrefs.getString(MMSDKConstants.KEY_OAUTH_PROVIDER, MMSDKConstants.DEFAULT_STRING_EMPTY));
		} else {
			MMAdapter.useMobMonkey(MMConstants.PARTNER_ID,
								   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
								   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
		}
		
		registerReceiver(mHandleMessageReceiver, new IntentFilter(MMSDKConstants.INTENT_FILTER_DISPLAY_MESSAGE));
		registerGCM();
		
		tabWidget = getTabWidget();
		tabHost = getTabHost();
		
		getAllCategories();
		getAllFavorites();
		checkUserIn();
	}
	
	/**
	 * Function that registers the Google Cloud Message service
	 */
	private void registerGCM() {
		GCMRegistrar.checkDevice(MainScreen.this);
		GCMRegistrar.checkManifest(MainScreen.this);
		
		String regId = GCMRegistrar.getRegistrationId(MainScreen.this);
		Log.d(TAG, TAG + "regId: " + regId);
		if (regId.equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
			GCMRegistrar.register(MainScreen.this, GCMIntentService.SENDER_ID);
		} else {
			MMGCMAdapter.registerGCMRegId(new RegisterGCMWithMobMonkeyCallback(),
										  MainScreen.this,
										  regId);
		}
	}
	
	/**
	 * Function that set the tabs and the corresponding {@link Activity} for the {@link TabHost}
	 */
	private void setTabs() {
		addTab(MMSDKConstants.TAB_TITLE_TRENDING_NOW, R.drawable.tab_trendingnow, TrendingNowActivity.class);
		addTab(MMSDKConstants.TAB_TITLE_INBOX, R.drawable.tab_inbox, InboxActivity.class);
		addTab(MMSDKConstants.TAB_TITLE_SEARCH, R.drawable.tab_search, SearchLocationsActivity.class);
		addTab(MMSDKConstants.TAB_TITLE_FAVORITES, R.drawable.tab_favorites, FavoritesActivity.class);
		addTab(MMSDKConstants.TAB_TITLE_SETTINGS, R.drawable.tab_settings, SettingsActivity.class);
		tabHost.setCurrentTab(userPrefs.getInt(MMSDKConstants.TAB_TITLE_CURRENT_TAG, 0));
	}
	
	/**
	 * Add the tab to the existing {@link TabHost} object
	 * @param tabTitle Title of the tab
	 * @param drawableIconId Tab icon drawable resource id
	 * @param c {@link Class} instance of the screen to be displayed for the tab
	 */
	private void addTab(String tabTitle, int drawableIconId, Class<?> c) {
		Intent intent = new Intent(MainScreen.this, c);
		TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab" + tabTitle);
		
		LayoutInflater layoutInflater = LayoutInflater.from(MainScreen.this);
		View tabIndicator = layoutInflater.inflate(R.layout.tab_indicator, tabWidget, false);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.ivtabicon);
		icon.setImageResource(drawableIconId);
		
		tabSpec.setIndicator(tabIndicator);
		tabSpec.setContent(intent);
		tabHost.addTab(tabSpec);
	}
	
	/**
	 * Function to get all the categories from the server
	 */
	private void getAllCategories() {
		if(!userPrefs.contains(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES) && MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation() != null) {			
			MMCategoryAdapter.cancelGetAllCategories();
			MMCategoryAdapter.getAllCategories(new CategoriesCallback());
			if(MMProgressDialog.isProgressDialogNull() || !MMProgressDialog.isProgressDialogShowing()) {
				MMProgressDialog.displayDialog(MainScreen.this,
											   MMSDKConstants.DEFAULT_STRING_EMPTY,
											   getString(R.string.pd_loading) + getString(R.string.pd_ellipses));
			}
		}
	}
	
	/**
	 * Function to get all the user's favorites from the server
	 * NOTE: This is needed to check the location in location info
	 */
	private void getAllFavorites() {		
		if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation() != null) {
			MMFavoritesAdapter.cancelGetFavorites();
			MMFavoritesAdapter.getFavorites(new FavoritesCallback());
			if(MMProgressDialog.isProgressDialogNull() || !MMProgressDialog.isProgressDialogShowing()) {
				MMProgressDialog.displayDialog(MainScreen.this,
											   MMSDKConstants.DEFAULT_STRING_EMPTY,
											   getString(R.string.pd_loading) + getString(R.string.pd_ellipses));
			}
		}
	}
	
	/**
	 * Function to check user in at his/her current location when he/she signs in
	 */
	private void checkUserIn() {		
		if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation() != null) {
			MMCheckinAdapter.checkInUser(new CheckUserInCallback());
			if(MMProgressDialog.isProgressDialogNull() || !MMProgressDialog.isProgressDialogShowing()) {
				MMProgressDialog.displayDialog(MainScreen.this,
											   MMSDKConstants.DEFAULT_STRING_EMPTY,
											   getString(R.string.pd_loading) + getString(R.string.pd_ellipses));
			}
		} else {
			setTabs();
		}
	}
	
	/**
	 * Callback to handle the response after register with MobMonkey with GCM regId
	 * @author Dezapp, LLC
	 *
	 */
	private class RegisterGCMWithMobMonkeyCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				Log.d(TAG, TAG + "RegisterGCMWithMobMobnkeyCallback response: " + (String) obj);
				
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(MainScreen.this, getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					// TODO:
				}
			}
		}
	}
	
	/**
	 * Callback that gets all the categories
	 * @author Dezapp, LLC
	 *
	 */ 
	private class CategoriesCallback implements MMCallback {
		@Override
		public void processCallback(Object obj){			
			if(obj != null) {
				Log.d(TAG, TAG + "CategoriesCallback: " + ((String) obj));
				
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(MainScreen.this, getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObj = new JSONObject((String) obj);
						if(!jObj.has(MMSDKConstants.JSON_KEY_STATUS)) {
							userPrefsEditor.putString(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, (String) obj);
						}
						userPrefsEditor.commit();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * Callback to update the user's favorites list in app data after making get favorites call to the server
	 * @author Dezapp, LLC
	 *
	 */
	private class FavoritesCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				Log.d(TAG, TAG + "FavoritesCallback: " + ((String) obj));
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(MainScreen.this, getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						userPrefsEditor.putString(MMSDKConstants.SHARED_PREFS_KEY_FAVORITES, (String) obj);
						userPrefsEditor.commit();

						JSONObject jObj = new JSONObject((String) obj);
						if(jObj.has(MMSDKConstants.JSON_KEY_STATUS)) {
							Toast.makeText(MainScreen.this, jObj.getString(MMSDKConstants.JSON_KEY_DESCRIPTION), Toast.LENGTH_LONG).show();
							userPrefsEditor.remove(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES);
							userPrefsEditor.remove(MMSDKConstants.SHARED_PREFS_KEY_FAVORITES);
							userPrefsEditor.commit();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

//			setTabs();
		}
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class CheckUserInCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(MainScreen.this, getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					Log.d(TAG, TAG + "checkinuser response: " + (String) obj);
					try {
						JSONObject jObj = new JSONObject((String) obj);
						if(jObj.getString(MMSDKConstants.JSON_KEY_STATUS).equals(MMSDKConstants.RESPONSE_STATUS_UNAUTHORIZED_EMAIL)) {
							userPrefsEditor.remove(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES);
							userPrefsEditor.remove(MMSDKConstants.SHARED_PREFS_KEY_FAVORITES);
							userPrefsEditor.commit();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					setTabs();
				}
			}
		}
	}
}
