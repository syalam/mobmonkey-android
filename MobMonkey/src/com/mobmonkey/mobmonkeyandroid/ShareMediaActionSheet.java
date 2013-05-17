package com.mobmonkey.mobmonkeyandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * @author Dezapp, LLC
 *
 */
@SuppressLint("ResourceAsColor")
public class ShareMediaActionSheet extends Activity implements AnimationListener {
	private static final String TAG = "ShareMediaActionSheet: ";
	
	private Animation slideBottomIn;
	private Animation slideBottomOut;
	
	private LinearLayout llActionSheet;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.action_sheet_share_media);
		
		slideBottomIn = AnimationUtils.loadAnimation(ShareMediaActionSheet.this, R.anim.slide_bottom_in);
		slideBottomOut = AnimationUtils.loadAnimation(ShareMediaActionSheet.this, R.anim.slide_bottom_out);
		slideBottomOut.setAnimationListener(ShareMediaActionSheet.this);		
		
		llActionSheet = (LinearLayout) findViewById(R.id.llactionsheet);
		llActionSheet.startAnimation(slideBottomIn);
	}
	
	@Override
	public void onAnimationEnd(Animation animation) {
		finish();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		llActionSheet.clearAnimation();
		llActionSheet.startAnimation(slideBottomOut);
		Log.d(TAG, TAG + "started animation");
	}

	/**
	 * 
	 * @param view
	 */
	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btnsavetocameraroll:
				break;
			case R.id.btnflagforreview:
				break;
			case R.id.btncancel:
				setResult(RESULT_CANCELED);
				break;
		}
		
		llActionSheet.clearAnimation();
		llActionSheet.startAnimation(slideBottomOut);
	}
}
