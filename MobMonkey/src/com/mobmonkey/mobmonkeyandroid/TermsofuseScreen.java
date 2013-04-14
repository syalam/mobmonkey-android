package com.mobmonkey.mobmonkeyandroid;

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Android {@link Activity} displays the Term of Use agreement to the user
 * @author Dezapp, LLC
 *
 */
public class TermsofuseScreen extends Activity {
	private static final String TAG = "TermofuserScreen";	
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	private int requestCode;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_hold);
		setContentView(R.layout.terms_of_use_screen);
		
		TextView tvToS = (TextView) findViewById(R.id.tvtos);
		Button btnReject = (Button) findViewById(R.id.btnreject);
		Button btnAccept = (Button) findViewById(R.id.btnaccept);
		
		tvToS.setMovementMethod(new ScrollingMovementMethod());
		
		if(getIntent().getBooleanExtra(MMAPIConstants.KEY_INTENT_EXTRA_TOS_DISPLAY_BUTTON, false)){
			requestCode = getIntent().getIntExtra(MMAPIConstants.REQUEST_CODE, MMAPIConstants.DEFAULT_INT);
			btnReject.setVisibility(View.VISIBLE);
			btnAccept.setVisibility(View.VISIBLE);
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_bottom_out);
	}
	
	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btnreject:
				break;
			case R.id.btnaccept:
				onAcceptClick();
				break;
		}
		
		onBackPressed();
	}
	
	private void onAcceptClick() {
		switch(requestCode) {
			case MMAPIConstants.REQUEST_CODE_TOS_FACEBOOK:
				userPrefsEditor.putBoolean(MMAPIConstants.SHARED_PREFS_KEY_TOS_FACEBOOK, true);
				break;
			case MMAPIConstants.REQUEST_CODE_TOS_TWITTER:
				userPrefsEditor.putBoolean(MMAPIConstants.SHARED_PREFS_KEY_TOS_TWITTER, true);
				break;
		}
		
		userPrefsEditor.commit();
	}
}
