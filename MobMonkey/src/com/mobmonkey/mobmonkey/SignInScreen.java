package com.mobmonkey.mobmonkey;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkeyapi.adapters.MMSignInAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * @author Dezapp, LLC
 *
 */
public class SignInScreen extends Activity {
	private static final String TAG = "SignInScreen: ";
	
	ProgressDialog progressDialog;
	EditText etEmailAddress;
	EditText etPassword;
		
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signinscreen);
		init();
	}

	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btnsignin:
				signInNormal();
				break;
			case R.id.btnsigninfacebook:
				break;
			case R.id.btnsignintwitter:
				break;
			case R.id.btnsignup:
				startActivity(new Intent(SignInScreen.this, SignUpScreen.class));
				break;
		}
	}
	
	private void init() {
		etEmailAddress = (EditText) findViewById(R.id.etemailaddress);
		etPassword = (EditText) findViewById(R.id.etpassword);
	}
	
	private void signInNormal() {
		if(checkEmailAddress()) {
			MMSignInAdapter.signInUser(new SignInCallback(), etEmailAddress.getText().toString(), etPassword.getText().toString(), MMConstants.PARTNER_ID);
    		progressDialog = ProgressDialog.show(SignInScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_signing_in), true, false);
		}
	}
	
    /**
     * Function that check if the email address {@link EditText} field is valid and is not empty and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkEmailAddress() {
    	if(!TextUtils.isEmpty(etEmailAddress.getText())) {
    		return checkPassword();
    	} else {
    		displayAlert(R.string.alert_invalid_email_address);
    		return false;
    	}
    }

    /**
     * Function that check if the password {@link EditText} fields are valid and are not empty. In addition, it compare the passwords to determine if they are equal and and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkPassword() {
    	if(!TextUtils.isEmpty(etPassword.getText())) {
    		return true;
    	} else {
    		displayAlert(R.string.alert_invalid_password);
    		return false;
    	}
    }
    
	/**
	 * Display an {@link AlertDialog} with the associated message informing user that they forgot enter a certain input field.
	 * @param messageId String resource id of the message to be displayed
	 */
	private void displayAlert(int messageId) {
		new AlertDialog.Builder(SignInScreen.this)
			.setTitle(R.string.app_name)
			.setMessage(messageId)
			.setNeutralButton(android.R.string.ok, null)
			.show();
	}
	
	private class SignInCallback implements MMCallback {
		public void processCallback(Object obj) {
			if(progressDialog != null) {
				progressDialog.dismiss();
			}
			
			try {
				JSONObject response = new JSONObject((String) obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d(TAG, TAG + "response: " + (String) obj);
		}
	}
}
