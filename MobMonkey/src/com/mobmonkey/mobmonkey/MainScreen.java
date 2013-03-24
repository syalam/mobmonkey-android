package com.mobmonkey.mobmonkey;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
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
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.ServerUtility;
import com.mobmonkey.mobmonkeyapi.adapters.MMBookmarksAdapter;
import com.mobmonkey.mobmonkeyapi.adapters.MMCategoryAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

/**
 * Android {@link Activity} screen displays the signed in user portion of the application with different tabs.
 * @author Dezapp, LLC
 *
 */
public class MainScreen extends TabActivity {
	protected static final String TAG = "MainScreen: ";
	
    AsyncTask<Void, Void, Void> mRegisterTask;
	
	SharedPreferences userPrefs;
	SharedPreferences.Editor userPrefsEditor;
	
	TabWidget tabWidget;
	TabHost tabHost;
	
	ProgressDialog progressDialog;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, TAG + "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		checkForGPSAccess();
		init();
		getAllCategories();
		getAllBookmarks();
		setTabs();
		tabHost.setCurrentTab(0);
	}

	private void init() {
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		tabWidget = getTabWidget();
		tabHost = getTabHost();

	    
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		
		registerReceiver(mHandleMessageReceiver,
                new IntentFilter("com.mobmonkey.mobmonkey.DISPLAY_MESSAGE"));
		
		final String regId = GCMRegistrar.getRegistrationId(this);
		Log.d(TAG, regId);
		if (regId.equals("")) {
		  GCMRegistrar.register(this, GCMIntentService.SENDER_ID);
		  String a = GCMRegistrar.getRegistrationId(this);
		  Log.d(TAG, "regId: " + a);
		} else {
			final Context context = this;
            mRegisterTask = new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    boolean registered =
                            ServerUtility.register(context, regId);
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

                @Override
                protected void onPostExecute(Void result) {
                    mRegisterTask = null;
                }

            };
            mRegisterTask.execute(null, null, null);
		}
		
	}
	
	/**
	 * Function that gets all the categories from the server
	 */
	private void getAllCategories() 
	{	
		
		Log.d(TAG, "getAllCategories: " + userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, ""));
		
		if(!userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES))
		{			
			progressDialog = ProgressDialog.show(MainScreen.this, MMAPIConstants.DEFAULT_STRING, "Loading...");

			MMCategoryAdapter.getAllCategories(
					new MainCallback(), 
					userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
					userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), 
					MMConstants.PARTNER_ID);
		} 
	}
	
	/**
	 * Function that set the tabs and the corresponding {@link Activity} for the {@link TabHost}
	 */
	private void setTabs() {
		addTab("Trending Now", R.drawable.tab_trendingnow, TrendingNowScreen.class);
		addTab("Inbox", R.drawable.tab_inbox, InboxScreen.class);
		addTab("Search", R.drawable.tab_search, SearchScreen.class);
		addTab("Bookmarks", R.drawable.tab_bookmarks, BookmarksScreen.class);
		addTab("Settings", R.drawable.tab_settings, SettingsScreen.class);
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
	 * Callback that gets all the category information
	 * @author Dezapp, LLC
	 * @param obj obj is the JSON response from the server
	 */ 
	private class MainCallback implements MMCallback {
		@Override
		public void processCallback(Object obj){
			
			userPrefsEditor.putString(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, (String) obj);
			userPrefsEditor.commit();
			progressDialog.dismiss();
		}
	}
	
	private void getAllBookmarks() {
		MMBookmarksAdapter.getBookmarks(new bookmarksCallback(), 
										"bookmarks", 
										MMConstants.PARTNER_ID, 
										userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
										userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
	}
	
	private class bookmarksCallback implements MMCallback {

		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				userPrefsEditor.putString(MMAPIConstants.SHARED_PREFS_KEY_BOOKMARKS, (String) obj);
			}
		}
	}
	
	private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString("message");
            Toast.makeText(MainScreen.this, newMessage, Toast.LENGTH_LONG).show();
            //mDisplay.append(newMessage + "\n");
        }
    };

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == MMAPIConstants.REQUEST_CODE_TURN_ON_GPS_ADD_LOCATION) {
			LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
			if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				checkForGPSAccess();
			} else {
				Toast.makeText(MainScreen.this, R.string.toast_not_enable_gps, Toast.LENGTH_SHORT).show();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
    
	/**
	 * Function that check if user's device has GPS access. Display a {@link Toast} message informing the user if 
	 * there is no GPS access.
	 */
	private void checkForGPSAccess() {
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			new AlertDialog.Builder(MainScreen.this)
	    	.setTitle(R.string.ad_title_enable_gps)
	    	.setMessage(R.string.ad_message_enable_gps)
	    	.setCancelable(false)
	    	.setPositiveButton(R.string.ad_btn_yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            // Launch settings, allowing user to make a change
		            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), MMAPIConstants.REQUEST_CODE_TURN_ON_GPS_ADD_LOCATION);
		        }
	    	})
	    	.setNegativeButton(R.string.ad_btn_no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            // No location service, no Activity
		        	Toast.makeText(MainScreen.this, R.string.toast_not_enable_gps, Toast.LENGTH_SHORT).show();
//		            finish();
		            // TODO: not close activity but return back to previous tab
		        }
	    	})
	    	.show();
	    }
	}
}
