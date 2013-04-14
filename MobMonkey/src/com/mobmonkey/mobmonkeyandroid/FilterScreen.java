package com.mobmonkey.mobmonkeyandroid;

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ToggleButton;

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
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_hold);
		setContentView(R.layout.filter_screen);
		
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		RadioGroup rgDist = (RadioGroup) findViewById(R.id.rgdist);
		
		rgDist.check(getCheckedRadioButton());
		rgDist.setOnCheckedChangeListener(FilterScreen.this);
		
		ToggleButton tbNarrowByLiveVideo = (ToggleButton) findViewById(R.id.tbnarrowbylivevideo);
		tbNarrowByLiveVideo.setChecked(userPrefs.getBoolean(MMAPIConstants.SHARED_PREFS_KEY_NARROW_BY_LIVE_VIDEO, false));
		
		tbNarrowByLiveVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				userPrefsEditor.putBoolean(MMAPIConstants.SHARED_PREFS_KEY_NARROW_BY_LIVE_VIDEO, isChecked);
			}
		});
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int searchRadius = MMAPIConstants.SEARCH_RADIUS_HALF_MILE;
		
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

	@Override
	public void onBackPressed() {
		userPrefsEditor.commit();
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_bottom_out);
	}
	
	private int getCheckedRadioButton() {
		int searchRadius = userPrefs.getInt(MMAPIConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMAPIConstants.SEARCH_RADIUS_HALF_MILE);
		
		switch(searchRadius) {
			case MMAPIConstants.SEARCH_RADIUS_HALF_MILE:
				return R.id.rbhalfmile;
			case MMAPIConstants.SEARCH_RADIUS_ONE_MILE:
				return R.id.rbonemile;
			case MMAPIConstants.SEARCH_RADIUS_FIVE_MILE:
				return R.id.rbfivemile;
			case MMAPIConstants.SEARCH_RADIUS_TEN_MILE:
				return R.id.rbtenmile;
			case MMAPIConstants.SEARCH_RADIUS_TWENTY_MILE:
				return R.id.rbtwentymile;
			default:
				return R.id.rbhalfmile;
		}
	}
}
