package com.mobmonkey.mobmonkey;

import java.util.UUID;

import com.mobmonkey.mobmonkeyapi.servercalls.MMSignUp;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMConstants;

import android.os.Bundle;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.telephony.TelephonyManager;

public class SignUpScreen extends Activity {	
	TextView tvResponse;
	UUID deviceUUID;
	
	ProgressDialog progressDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signupscreen);
        tvResponse = (TextView) findViewById(R.id.tvresponse);
        
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        
        String tmDevice = telephonyManager.getDeviceId();
        String tmSerial = telephonyManager.getSimSerialNumber();
        String androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        deviceUUID = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void signUp(View view) {
    	switch(view.getId()) {
	    	case R.id.btnsignup:
	        	progressDialog = ProgressDialog.show(SignUpScreen.this, MMConstants.DEFAULT_STRING, "Signing up...", true, true);
	        	MMSignUp.signUpNewUser(new SignUpCallback(), deviceUUID);
	    		break;
	    	case R.id.btnsignupfacebook:
	    		break;
	    	case R.id.btnsignuptwitter:
	    		break;
    	}
    }
    
    private class SignUpCallback implements MMCallback {
		public void processCallback(Object obj) {
			if(progressDialog != null) {
				progressDialog.dismiss();
			}
			tvResponse.setText((String) obj);
		}	
    }
}
