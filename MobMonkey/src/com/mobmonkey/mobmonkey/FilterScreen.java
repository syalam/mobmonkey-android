package com.mobmonkey.mobmonkey;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * @author Dezapp, LLC
 *
 */
public class FilterScreen extends Activity implements OnCheckedChangeListener {
	SharedPreferences userPrefs;
	SharedPreferences.Editor userPrefsEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filterscreen);
		
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		RadioGroup rgDist = (RadioGroup) findViewById(R.id.rgdist);
		
		rgDist.setOnCheckedChangeListener(FilterScreen.this);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int searchRadius = 0;
		
		switch(checkedId) {
			case R.id.rbhalfmile:
				searchRadius = MMAPIConstants.SEARCH_RADIUS_HALF_MILE;
				break;
			case R.id.rbonemile:
				searchRadius = MMAPIConstants.SEARCH_RADIUS_ONE_MILE;
				break;
			case R.id.rbfivemile:
				searchRadius = MMAPIConstants.SEARCH_RADIUS_FIVE_MILE;
				break;
			case R.id.rbtenmile:
				searchRadius = MMAPIConstants.SEARCH_RADIUS_TEN_MILE;
				break;
			case R.id.rbtwentymile:
				searchRadius = MMAPIConstants.SEARCH_RADIUS_TWENTY_MILE;
				break;
		}
		
		userPrefsEditor.putInt(MMAPIConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, searchRadius);
	}
}
