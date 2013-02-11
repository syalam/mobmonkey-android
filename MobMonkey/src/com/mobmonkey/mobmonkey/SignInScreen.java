package com.mobmonkey.mobmonkey;

import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkeyapi.adapters.MMSignInAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMGetDeviceUUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
//	SharedPreferences.Editor userPrefsEditor;
	
	ProgressDialog progressDialog;
	EditText etEmailAddress;
	EditText etPassword;
		
	private String userEmail;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // TODO: move this to the first screen of the app 
        MMGetDeviceUUID.setContext(getApplicationContext());
		
		setContentView(R.layout.signinscreen);
		init();
		userPrefs = getSharedPreferences("USER_PREFS", MODE_PRIVATE);
	}

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
	
//	private boolean requestEmail;
	private void signInFacebook() {
		MMSignInAdapter.signInUserFacebook(new SignInCallback(), "fakeone", userPrefs.getString("FBUserName", ""), MMConstants.PARTNER_ID);
//		Session.openActiveSession(SignInScreen.this, true, new Session.StatusCallback() {
//			public void call(Session session, SessionState state, Exception exception) {
//				if(session.isOpened() && requestEmail) {
//		    		Session.NewPermissionsRequest request = new Session.NewPermissionsRequest(SignInScreen.this, Arrays.asList("email"));
//					session.requestNewReadPermissions(request);
//					requestEmail = false;
//					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
//						public void onCompleted(GraphUser user, Response response) {
//							if(user != null) {
//								Log.d(TAG, TAG + "graphUser: " + user.getUsername());
//								Log.d(TAG, TAG + "user: " + user.getProperty("email"));
//								userEmail = (String) user.getProperty("email");
//							}
//						}
//					});
//				}
//			}
//		});
		
	}
	
	private void signInTwitter() {
		
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
				if(response.getString("status").equals("Success")) {
					Toast.makeText(SignInScreen.this, R.string.toast_sign_up_successful, Toast.LENGTH_SHORT).show();
					startActivity(new Intent(SignInScreen.this, SettingsScreen.class));
					finish();
				} else {
					// TODO: alert user
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d(TAG, TAG + "response: " + (String) obj);
		}
	}
	
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
//		Log.d(TAG, TAG + "Access Token: " + Session.getActiveSession().getAccessToken());
//		for(String perm : Session.getActiveSession().getPermissions()) {
//			Log.d(TAG, TAG + "perm: " + perm);
//		}
//		Session.NewPermissionsRequest request = new Session.NewPermissionsRequest(SignUpScreen.this, Arrays.asList("email"));
//		Session session = Session.getActiveSession();
//		
//		if(session == null) {
//			session = new Session(this);
//		}
//		
//		session.requestNewReadPermissions(request);
//		Session.setActiveSession(session);
//		session.openForRead(new Session.OpenRequest(this).setCallback(new Session.StatusCallback() {
//			public void call(Session session, SessionState state, Exception exception) {
//				Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
//					public void onCompleted(GraphUser user, Response response) {
//						if(user != null) {
//							Log.d(TAG, TAG + "user: " + user.getUsername());
//						}
//					}
//				});
//			}
//		}));
//		MMSignInAdapter.signInUserFacebook(new SignInCallback(), Session.getActiveSession().getAccessToken(), userEmail, MMConstants.PARTNER_ID);
//	}
}
