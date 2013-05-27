package com.mobmonkey.mobmonkeyandroid;

import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Dezapp, LLC
 *
 */
public class SplashScreen extends Activity {
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.splash_screen);
		MMLocationManager.setContext(getApplicationContext(), new MMLocationListener());
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(checkForInternetAccess()) {
//					startActivity(new Intent(SplashScreen.this, SignInScreen.class));
//					finish();
					checkForGPSAccess();
				}
			}
		}, 1000);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		Log.d(TAG, TAG + ":onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == MMSDKConstants.REQUEST_CODE_TURN_ON_GPS_LOCATION) {
			if(MMLocationManager.isGPSEnabled()) {
				checkForGPSAccess();
			} else {
				noGPSEnabled();
			}
		}
	}
	
	/**
	 * Function that check if user's device has Internet access. Display a {@link Toast} message informing the user if these is no Internet access.
	 */
	private boolean checkForInternetAccess() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivityManager.getActiveNetworkInfo() == null || !connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()) {
			new AlertDialog.Builder(SplashScreen.this)
				.setTitle(R.string.ad_title_no_internet_access)
				.setMessage(R.string.ad_message_no_internet_access)
				.setNeutralButton(R.string.ad_btn_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.show();
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Function that check if user's device has GPS access. Display a {@link Toast} message informing the user if 
	 * there is no GPS access.
	 */
	private void checkForGPSAccess() {
		if(!MMLocationManager.isGPSEnabled()) {
			new AlertDialog.Builder(SplashScreen.this)
	    	.setTitle(R.string.ad_title_enable_gps)
	    	.setMessage(R.string.ad_message_enable_gps)
	    	.setCancelable(false)
	    	.setPositiveButton(R.string.ad_btn_yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            // Launch settings, allowing user to make a change
		            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), MMSDKConstants.REQUEST_CODE_TURN_ON_GPS_LOCATION);
		        }
	    	})
	    	.setNegativeButton(R.string.ad_btn_no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	noGPSEnabled();
		        }
	    	})
	    	.show();
	    } else {
			startActivity(new Intent(SplashScreen.this, SignInScreen.class));
			finish();
	    }
	}
	
	/**
	 * Function that create an {@link AlertDialog} to the user if the GPS is not enabled alerting them some features are not accessible without GPS
	 */
	private void noGPSEnabled() {
    	new AlertDialog.Builder(SplashScreen.this)
	    	.setIcon(android.R.drawable.ic_dialog_alert)
	    	.setTitle(R.string.ad_title_no_gps_warning)
	    	.setMessage(R.string.ad_message_no_gps)
	    	.setCancelable(false)
	    	.setNeutralButton(R.string.ad_btn_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivity(new Intent(SplashScreen.this, SignInScreen.class));
					finish();
				}
			})
	    	.show();
	}
}
