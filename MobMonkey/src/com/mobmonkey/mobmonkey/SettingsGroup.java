package com.mobmonkey.mobmonkey;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * @author Dezapp, LLC
 *
 */
public class SettingsGroup extends ActivityGroup {
	private static final String TAG = "SettingsGroup: ";
	public static SettingsGroup settingsGroup;
	
	private ArrayList history;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		history = new ArrayList();
		settingsGroup = SettingsGroup.this;
		
		View view = getLocalActivityManager().startActivity("SettingsScreen", new Intent(SettingsGroup.this, SettingsScreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
		
		replaceView(view);
	}
		
	@Override
	public void onBackPressed() {
		Log.d(TAG, TAG + "onBackPressed");
		SettingsGroup.settingsGroup.back();
		return;
	}

	public void replaceView(View view) {
		history.add(view);
		setContentView(view);
	}
	
	public void back() {
		if(history.size() > 1) {
			history.remove(history.size() - 1);
			setContentView((View) history.get(history.size() - 1));
		} else {
			// Do nothing since we don't want to exit this screen;
		}
	}
}
