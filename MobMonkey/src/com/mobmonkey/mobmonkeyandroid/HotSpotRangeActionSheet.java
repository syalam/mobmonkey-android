package com.mobmonkey.mobmonkeyandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
public class HotSpotRangeActionSheet extends Activity implements AnimationListener {
	Animation slideBottomIn;
	Animation slideBottomOut;
	
	LinearLayout llActionSheet;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.action_sheet_hot_spot_range);
		
		slideBottomIn = AnimationUtils.loadAnimation(HotSpotRangeActionSheet.this, R.anim.slide_bottom_in);
		slideBottomOut = AnimationUtils.loadAnimation(HotSpotRangeActionSheet.this, R.anim.slide_bottom_out);
		slideBottomOut.setAnimationListener(HotSpotRangeActionSheet.this);		
		
		llActionSheet = (LinearLayout) findViewById(R.id.llactionsheet);
		llActionSheet.setAnimation(slideBottomIn);
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
		llActionSheet.setAnimation(slideBottomOut);
	}

	/**
	 * 
	 * @param view
	 */
	public void viewOnClick(View view) {
		Intent intent = new Intent();
		switch(view.getId()) {
			case R.id.btnfivemeters:
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_RANGE, MMSDKConstants.RANGE_FIVE_METERS);
				setResult(RESULT_OK, intent);
				break;
			case R.id.btntenmeters:
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_RANGE, MMSDKConstants.RANGE_TEN_METERS);
				setResult(RESULT_OK, intent);
				break;
			case R.id.btnthirtymeters:
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_RANGE, MMSDKConstants.RANGE_THIRTY_METERS);
				setResult(RESULT_OK, intent);
				break;
			case R.id.btnfiftymeters:
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_RANGE, MMSDKConstants.RANGE_FIFTY_METERS);
				setResult(RESULT_OK, intent);
				break;
			case R.id.btnhundredmeters:
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_RANGE, MMSDKConstants.RANGE_HUNDRED_METERS);
				setResult(RESULT_OK, intent);
				break;
			case R.id.btncancel:
				setResult(RESULT_CANCELED);
				break;
		}
		
		llActionSheet.clearAnimation();
		llActionSheet.setAnimation(slideBottomOut);
	}
}
