package com.mobmonkey.mobmonkey;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;

/**
 * @author Dezapp, LLC
 *
 */
public class MainScreen extends TabActivity {
	protected static final String TAG = "TAG ";
	TabWidget tabWidget;
	TabHost tabHost;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		
		tabWidget = getTabWidget();
		tabHost = getTabHost();
		setTabs();
		tabHost.setCurrentTab(0);
	}

	/**
	 * 
	 */
	private void setTabs() {
		addTab("Trending Now", R.drawable.tab_trendingnow, TrendingNowScreen.class);
		addTab("Inbox", R.drawable.tab_inbox, InboxScreen.class);
		addTab("Search", R.drawable.tab_search, SearchScreen.class);
		addTab("Bookmarks", R.drawable.tab_bookmarks, BookmarksScreen.class);
		addTab("Settings", R.drawable.tab_settings, SettingsScreen.class);
	}
	
	/**
	 * 
	 * @param tabTitle
	 * @param drawableIconId
	 * @param c
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
}
