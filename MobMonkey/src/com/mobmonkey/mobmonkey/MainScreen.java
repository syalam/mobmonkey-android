package com.mobmonkey.mobmonkey;

import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkeyapi.adapters.MMCategoryAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;

/**
 * Android {@link Activity} screen displays the signed in user portion of the application with different tabs.
 * @author Dezapp, LLC
 *
 */
public class MainScreen extends TabActivity {
	protected static final String TAG = "MainScreen: ";
	
	SharedPreferences userPrefs;
	SharedPreferences.Editor userPrefsEditor;
	
	TabWidget tabWidget;
	TabHost tabHost;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, TAG + "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		
		init();
		getTopLevelCategories();
		setTabs();
		tabHost.setCurrentTab(0);
	}

	private void init() {
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		tabWidget = getTabWidget();
		tabHost = getTabHost();
	}
	
	private void getTopLevelCategories() {
		if(!userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_TOP_LEVEL_CATEGORIES)) {
			MMCategoryAdapter.getTopLevelCategories(new MainCallback(), 
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
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class MainCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				userPrefsEditor.putString(MMAPIConstants.SHARED_PREFS_KEY_TOP_LEVEL_CATEGORIES, (String) obj);
				userPrefsEditor.commit();
			}
			Log.d(TAG, TAG + "Categories response: " + (String) obj);
		}
	}
}
