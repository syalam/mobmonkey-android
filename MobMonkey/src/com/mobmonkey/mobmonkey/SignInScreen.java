package com.mobmonkey.mobmonkey;

import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.LoginActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkeyapi.adapters.MMSignInAdapter;
import com.mobmonkey.mobmonkeyapi.adapters.MMSignUpAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMDeviceUUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Dezapp, LLC
 *
 */
public class SignInScreen extends Activity {
	private static final String TAG = "SignInScreen: ";
	
	SharedPreferences userPrefs;
	SharedPreferences.Editor userPrefsEditor;
	
	ProgressDialog progressDialog;
	EditText etEmailAddress;
	EditText etPassword;
	
    boolean requestEmail;
	GraphUser facebookUser;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivityManager.getActiveNetworkInfo() == null || !connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()) {
			Toast.makeText(SignInScreen.this, "You have no internet access at the moment, cannot start MobMonkey", Toast.LENGTH_LONG).show();
			finish();
		}
		
        if (android.os.Build.VERSION.SDK_INT > 9) {
        	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        }
		
		super.onCreate(savedInstanceState);
        MMDeviceUUID.setContext(getApplicationContext());
		
		setContentView(R.layout.signin_screen);
		init();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, TAG + "onActivityResult");
		
		if(requestCode == MMAPIConstants.REQUEST_CODE_SIGN_IN_TWITTER_AUTH) {
			if(resultCode == MMAPIConstants.RESULT_CODE_SUCCESS) {
				Toast.makeText(SignInScreen.this, R.string.toast_sign_in_successful, Toast.LENGTH_SHORT).show();
				Log.d(TAG, TAG + "twitter provider username: " + userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING));
				Log.d(TAG, TAG + "twitter oauth token: " + userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
				startActivity(new Intent(SignInScreen.this, MainScreen.class));
			} else if(resultCode == MMAPIConstants.RESULT_CODE_NOT_FOUND) {
				// TODO: inform user
			}
		} else {
			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
			if(!requestEmail) {
				userPrefsEditor.putString(MMAPIConstants.KEY_USER, (String) facebookUser.getProperty(MMAPIConstants.FACEBOOK_REQ_PERM_EMAIL));
				userPrefsEditor.putString(MMAPIConstants.KEY_AUTH, 	Session.getActiveSession().getAccessToken());
				MMSignUpAdapter.signUpNewUserFacebook(new SignInCallback(), Session.getActiveSession().getAccessToken(), 
						(String) facebookUser.getProperty(MMAPIConstants.FACEBOOK_REQ_PERM_EMAIL), MMConstants.PARTNER_ID);
	    		progressDialog = ProgressDialog.show(SignInScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_signing_in_facebook), true, false);
			}
		}
	}
	
	/**
	 * 
	 * @param view
	 */
	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btnsignin:
				signInNormal();
				break;
			case R.id.btnsigninfacebook:
				signInFacebook();
				break;
			case R.id.btnsignintwitter:
				signInTwitter();
				break;
			case R.id.btnsignup:
				signUp();
				break;
		}
	}

	/**
	 * 
	 */
	private void init() {
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		etEmailAddress = (EditText) findViewById(R.id.etemailaddress);
		etPassword = (EditText) findViewById(R.id.etpassword);
		
		requestEmail = true;
		
		// TODO: hardcoded values, to be removed
		etEmailAddress.setText("duds411@yahoo.com");
		etPassword.setText("helloworld123");
	}
	
	/**
	 * 
	 */
	private void signInNormal() {
		if(checkEmailAddress()) {
			userPrefsEditor.putString(MMAPIConstants.KEY_USER, etEmailAddress.getText().toString());
			userPrefsEditor.putString(MMAPIConstants.KEY_AUTH, etPassword.getText().toString());
			MMSignInAdapter.signInUser(new SignInCallback(), etEmailAddress.getText().toString(), etPassword.getText().toString(), MMConstants.PARTNER_ID);
    		progressDialog = ProgressDialog.show(SignInScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_signing_in), true, false);
		}
	}
	
	/**
	 * 
	 */
	private void signInFacebook() {
		Session.openActiveSession(SignInScreen.this, true, new Session.StatusCallback() {
			public void call(Session session, SessionState state, Exception exception) {
    			Log.d(TAG, TAG + "sign in with facebook");
    			Log.d(TAG, TAG + "requestEmail: " + requestEmail);
    			Log.d(TAG, TAG + "session opened: " + session.isOpened());
				if(session.isOpened() && requestEmail) {
		    		Session.NewPermissionsRequest request = new Session.NewPermissionsRequest(SignInScreen.this, Arrays.asList(MMAPIConstants.FACEBOOK_REQ_PERM_EMAIL));
					session.requestNewReadPermissions(request);
					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
						public void onCompleted(GraphUser user, Response response) {
							Log.d(TAG, TAG + "onCompleted");
							if(user != null) {
								requestEmail = false;
								facebookUser = user;
							}
						}
					});
				}
			}
		});	
	}
	
	/**
	 * 
	 */
	private void signInTwitter() {
		Intent twitterAuthIntent = new Intent(SignInScreen.this, TwitterAuthScreen.class);
		twitterAuthIntent.putExtra(MMAPIConstants.REQUEST_CODE, MMAPIConstants.REQUEST_CODE_SIGN_IN_TWITTER_AUTH);
		startActivityForResult(twitterAuthIntent, MMAPIConstants.REQUEST_CODE_SIGN_IN_TWITTER_AUTH);
	}
	
	/**
	 * 
	 */
	private void signUp() {
		Log.d(TAG, TAG + "SignUp");
		startActivity(new Intent(SignInScreen.this, SignUpScreen.class));
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
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class SignInCallback implements MMCallback {
		public void processCallback(Object obj) {
			if(progressDialog != null) {
				progressDialog.dismiss();
			}
			
			try {
				JSONObject response = new JSONObject((String) obj);
				if(response.getString(MMAPIConstants.KEY_RESPONSE_ID).equals(MMAPIConstants.RESPONSE_ID_SUCCESS)) {
					Toast.makeText(SignInScreen.this, R.string.toast_sign_in_successful, Toast.LENGTH_SHORT).show();
					if(requestEmail == false) {
						requestEmail = true;
					}
					userPrefsEditor.commit();
					startActivity(new Intent(SignInScreen.this, MainScreen.class));
				} else {
					Toast.makeText(SignInScreen.this, response.getString(MMAPIConstants.KEY_RESPONSE_DESC), Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d(TAG, TAG + "response: " + (String) obj);
		}
	}
}
