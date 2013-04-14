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
import android.os.AsyncTask;
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
import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkeyandroid.utils.GCMIntentService;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.ServerUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMCategoryAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMFavoritesAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
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
            String newMessage = intent.getExtras().getString("message");
            Toast.makeText(MainScreen.this, newMessage, Toast.LENGTH_LONG).show();
            //mDisplay.append(newMessage + "\n");
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
		MMLocationManager.setContext(getApplicationContext());
		checkForGPSAccess();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == MMAPIConstants.REQUEST_CODE_TURN_ON_GPS_LOCATION) {
			if(MMLocationManager.isGPSEnabled()) {
				checkForGPSAccess();
			} else {
				noGPSEnabled();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.ActivityGroup#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		unregisterReceiver(mHandleMessageReceiver);
		super.onDestroy();
	}
	
	/**
	 * Function that check if user's device has GPS access. Display a {@link Toast} message informing the user if 
	 * there is no GPS access.
	 */
	private void checkForGPSAccess() {
		if(!MMLocationManager.isGPSEnabled()) {
			new AlertDialog.Builder(MainScreen.this)
	    	.setTitle(R.string.ad_title_enable_gps)
	    	.setMessage(R.string.ad_message_enable_gps)
	    	.setCancelable(false)
	    	.setPositiveButton(R.string.ad_btn_yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            // Launch settings, allowing user to make a change
		            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), MMAPIConstants.REQUEST_CODE_TURN_ON_GPS_LOCATION);
		        }
	    	})
	    	.setNegativeButton(R.string.ad_btn_no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	noGPSEnabled();
		        }
	    	})
	    	.show();
	    } else if(MMLocationManager.getGPSLocation(new MMLocationListener()) == null) {
			new AlertDialog.Builder(MainScreen.this)
	    	.setTitle(R.string.ad_title_no_location)
	    	.setMessage(R.string.ad_message_no_location)
	    	.setCancelable(false)
	    	.setNeutralButton(R.string.ad_btn_ok, null)
	    	.show();
	    } else {
			init();
	    }
	}
	
	/**
	 * Function that create an {@link AlertDialog} to the user if the GPS is not enabled alerting them some features are not accessible without GPS
	 */
	private void noGPSEnabled() {
    	new AlertDialog.Builder(MainScreen.this)
	    	.setIcon(android.R.drawable.ic_dialog_alert)
	    	.setTitle(R.string.ad_title_no_gps_warning)
	    	.setMessage(R.string.ad_message_no_gps)
	    	.setCancelable(false)
	    	.setNeutralButton(R.string.ad_btn_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					init();
				}
			})
	    	.show();
	}
	
	/**
	 * Initialize all the variables to be used in {@link MainScreen}
	 */
	private void init() {		
		registerReceiver(mHandleMessageReceiver, new IntentFilter(MMAPIConstants.INTENT_FILTER_DISPLAY_MESSAGE));
		registerGCM();
		
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		tabWidget = getTabWidget();
		tabHost = getTabHost();
		
		getAllCategories();
		getAllFavorites();
		tabHost.setCurrentTab(0);
	}
	
	/**
	 * Function that registers the Google Cloud Message service
	 */
	private void registerGCM() {
		GCMRegistrar.checkDevice(MainScreen.this);
		GCMRegistrar.checkManifest(MainScreen.this);
		
		final String regId = GCMRegistrar.getRegistrationId(MainScreen.this);
		Log.d(TAG, TAG + "regId: " + regId);
		if (regId.equals(MMAPIConstants.DEFAULT_STRING)) {
			GCMRegistrar.register(MainScreen.this, GCMIntentService.SENDER_ID);
			String a = GCMRegistrar.getRegistrationId(MainScreen.this);
			Log.d(TAG, TAG + "GCMRegistrar regId: " + a);
		} else {
			new RegisterGCMAsyncTask(MainScreen.this).execute(regId);
		}
	}
	
	/**
	 * Function that set the tabs and the corresponding {@link Activity} for the {@link TabHost}
	 */
	private void setTabs() {
		addTab(MMAPIConstants.TAB_TITLE_TRENDING_NOW, R.drawable.tab_trendingnow, TrendingNowActivity.class);
		addTab(MMAPIConstants.TAB_TITLE_INBOX, R.drawable.tab_inbox, InboxActivity.class);
		addTab(MMAPIConstants.TAB_TITLE_SEARCH, R.drawable.tab_search, SearchActivity.class);
		addTab(MMAPIConstants.TAB_TITLE_FAVORITES, R.drawable.tab_favorites, FavoritesActivity.class);
		addTab(MMAPIConstants.TAB_TITLE_SETTINGS, R.drawable.tab_settings, SettingsActivity.class);
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
		Log.d(TAG, TAG + "getAllCategories: " + userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, MMAPIConstants.DEFAULT_STRING));
		
		if(!userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES) && MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
			MMProgressDialog.displayDialog(MainScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_loading) + getString(R.string.pd_ellipses));

			MMCategoryAdapter.getAllCategories(
					new CategoriesCallback(), 
					userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
					userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), 
					MMConstants.PARTNER_ID);
		}
	}
	
	/**
	 * Function to get all the user's favorites from the server
	 */
	private void getAllFavorites() {		
		if(MMProgressDialog.isProgressDialogNull() || !MMProgressDialog.isProgressDialogShowing()) {
			MMProgressDialog.displayDialog(MainScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_loading) + getString(R.string.pd_ellipses));
		}
		
		if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
			MMFavoritesAdapter.getFavorites(new FavoritesCallback(),
											MMConstants.PARTNER_ID, 
											userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
											userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
		} else {
			setTabs();
			MMProgressDialog.dismissDialog();
		}
	}
	
	/**
	 * {@link AsyncTask} to register Google Cloud Message in the background
	 * @author Dezapp, LLC
	 *
	 */
	private class RegisterGCMAsyncTask extends AsyncTask<String, Void, Void> {
		Context context;
		
		public RegisterGCMAsyncTask(Context context) {
			this.context = context;
		}
		
		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			boolean registered = ServerUtility.register(context, params[0]);
            // At this point all attempts to register with the app
            // server failed, so we need to unregister the device
            // from GCM - the app will try to register again when
            // it is restarted. Note that GCM will send an
            // unregistered callback upon completion, but
            // GCMIntentService.onUnregistered() will ignore it.
            if (!registered) {
                GCMRegistrar.unregister(context);
            }
			return null;
		}
	}
	
	/**
	 * Callback that gets all the category information
	 * @author Dezapp, LLC
	 *
	 */ 
	private class CategoriesCallback implements MMCallback {
		@Override
		public void processCallback(Object obj){			
			if(obj != null) {
				Log.d(TAG, TAG + "CategoriesCallback: " + ((String) obj));
				
				try {
					JSONObject jObj = new JSONObject((String) obj);
					
					if(!jObj.has(MMAPIConstants.JSON_KEY_STATUS)) {
						userPrefsEditor.putString(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, (String) obj);
					}
					userPrefsEditor.commit();
				} catch (JSONException e) {
					e.printStackTrace();
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
				MMProgressDialog.dismissDialog();
				Log.d(TAG, TAG + "FavoritesCallback: " + ((String) obj));
				try {
					userPrefsEditor.putString(MMAPIConstants.SHARED_PREFS_KEY_FAVORITES, (String) obj);
					userPrefsEditor.commit();
					
					JSONObject jObj = new JSONObject((String) obj);
					if(jObj.has(MMAPIConstants.JSON_KEY_STATUS)) {
						Toast.makeText(MainScreen.this, jObj.getString(MMAPIConstants.JSON_KEY_DESCRIPTION), Toast.LENGTH_LONG).show();
						userPrefsEditor.remove(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES);
						userPrefsEditor.remove(MMAPIConstants.SHARED_PREFS_KEY_FAVORITES);
						userPrefsEditor.commit();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			setTabs();
		}
	}
}
