package com.mobmonkey.mobmonkeyandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

/**
 * @author Dezapp, LLC
 *
 */
public class SplashScreen extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.splash_screen);
				
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(checkForInternetAccess()) {
					startActivity(new Intent(SplashScreen.this, SignInScreen.class));
					finish();
				}
			}
		}, 1000);
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
}
