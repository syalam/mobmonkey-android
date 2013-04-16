package com.mobmonkey.mobmonkeyandroid;

import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeysdk.adapters.MMUserAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMDeviceUUID;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Android {@link Activity} screen that allows user to sign with his/her account through MobMonkey, Facebook, Twitter or go sign up.
 * Also, it is the first {@link Activity} that the user will see when the application launches.
 * @author Dezapp, LLC
 *
 */
public class SignInScreen extends Activity {
	private static final String TAG = "SignInScreen: ";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	
	private EditText etEmailAddress;
	private EditText etPassword;
	
	private boolean requestEmail;
	private GraphUser facebookUser;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		checkForInternetAccess();
		
		// TODO: check if this is still needed...
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
		
		switch(requestCode) {
			case MMAPIConstants.REQUEST_CODE_TOS_FACEBOOK:
				if(userPrefs.getBoolean(MMAPIConstants.SHARED_PREFS_KEY_TOS_FACEBOOK, false)) {
					Session.openActiveSession(SignInScreen.this, true, new SessionStatusCallback());
				}
				break;
			case MMAPIConstants.REQUEST_CODE_TOS_TWITTER:
				if(userPrefs.getBoolean(MMAPIConstants.SHARED_PREFS_KEY_TOS_TWITTER, false)) {
					launchTwitterAuthScreen();
				}
				break;
			case MMAPIConstants.REQUEST_CODE_SIGN_IN_TWITTER_AUTH:
				MMProgressDialog.dismissDialog();
				
				if(resultCode == MMAPIConstants.RESULT_CODE_SUCCESS) {
					Toast.makeText(SignInScreen.this, R.string.toast_sign_in_successful, Toast.LENGTH_SHORT).show();
					startActivity(new Intent(SignInScreen.this, MainScreen.class));
				} else if(resultCode == MMAPIConstants.RESULT_CODE_NOT_FOUND) {
					Toast.makeText(SignInScreen.this, R.string.toast_new_twitter_user, Toast.LENGTH_SHORT).show();
					Intent signUpTwitterIntent = (Intent) data.clone();
					signUpTwitterIntent.setClass(SignInScreen.this, SignUpTwitterScreen.class);
					startActivityForResult(signUpTwitterIntent, MMAPIConstants.REQUEST_CODE_SIGN_UP_TWITTER);
				}
				break;
			case MMAPIConstants.REQUEST_CODE_SIGN_UP_TWITTER:
				if(resultCode == RESULT_OK) {
					startActivity(new Intent(SignInScreen.this, MainScreen.class));
				}
				break;
			default:
				Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
				if(!requestEmail) {
					userPrefsEditor.putString(MMAPIConstants.KEY_USER, (String) facebookUser.getProperty(MMAPIConstants.FACEBOOK_REQ_PERM_EMAIL));
					userPrefsEditor.putString(MMAPIConstants.KEY_AUTH, 	Session.getActiveSession().getAccessToken());
					userPrefsEditor.putString(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.OAUTH_PROVIDER_FACEBOOK);
					String emailAddress = (String)facebookUser.getProperty(MMAPIConstants.FACEBOOK_REQ_PERM_EMAIL);
					Log.d(TAG, TAG + "Email address: " + emailAddress);
					if(emailAddress == null) {
						requestEmail = true;
						Session.openActiveSession(SignInScreen.this, true, new SessionStatusCallback());
					} else {
						MMUserAdapter.signUpNewUserFacebook(new SignInCallback(), Session.getActiveSession().getAccessToken(), 
								(String) facebookUser.getProperty(MMAPIConstants.FACEBOOK_REQ_PERM_EMAIL), MMConstants.PARTNER_ID);
						MMProgressDialog.displayDialog(SignInScreen.this, MMAPIConstants.DEFAULT_STRING_EMPTY, getString(R.string.pd_signing_in_facebook));
					}
				}
		}
	}

	/**
	 * Handler for when {@link Button}s or any other {@link View}s are clicked.
	 * @param view {@link View} that is clicked
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
	 * Function that check if user's device has Internet access. Display a {@link Toast} message informing the user if these is no Internet access.
	 */
	private void checkForInternetAccess() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivityManager.getActiveNetworkInfo() == null || !connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()) {
			Toast.makeText(SignInScreen.this, getString(R.string.toast_no_internet_access), Toast.LENGTH_LONG).show();
			finish();
		}
	}

	
	/**
	 * Initialize all the variables to be used in {@link SignInScreen}
	 */
	private void init() {
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		etEmailAddress = (EditText) findViewById(R.id.etemailaddress);
		etPassword = (EditText) findViewById(R.id.etpassword);
		
		requestEmail = true;
		
		// TODO: hardcoded values, to be removed
		etEmailAddress.setText("hankyu1@yahoo.com");
		etPassword.setText("a1a2a3");
	}
	
	private void launchToS(int requestCode) {
		Intent openToSIntent = new Intent(SignInScreen.this, TermsofuseScreen.class);
		openToSIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_TOS_DISPLAY_BUTTON, true);
		openToSIntent.putExtra(MMAPIConstants.REQUEST_CODE, requestCode);
		startActivityForResult(openToSIntent, requestCode);
	}
	
	private void launchTwitterAuthScreen() {
		MMProgressDialog.displayDialog(SignInScreen.this, MMAPIConstants.DEFAULT_STRING_EMPTY, getString(R.string.pd_launch_twitter_auth_screen));
		Intent twitterAuthIntent = new Intent(SignInScreen.this, TwitterAuthScreen.class);
		twitterAuthIntent.putExtra(MMAPIConstants.REQUEST_CODE, MMAPIConstants.REQUEST_CODE_SIGN_IN_TWITTER_AUTH);
		startActivityForResult(twitterAuthIntent, MMAPIConstants.REQUEST_CODE_SIGN_IN_TWITTER_AUTH);
	}
	
	/**
	 * Functional that handles the normal user sign in with email through MobMonkey
	 */
	private void signInNormal() {
		if(checkEmailAddress()) {
			userPrefsEditor.putString(MMAPIConstants.KEY_USER, etEmailAddress.getText().toString());
			userPrefsEditor.putString(MMAPIConstants.KEY_AUTH, etPassword.getText().toString());
			userPrefsEditor.putString(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.DEFAULT_STRING_EMPTY);
			MMUserAdapter.signInUser(new SignInCallback(), etEmailAddress.getText().toString(), etPassword.getText().toString(), MMConstants.PARTNER_ID);
    		MMProgressDialog.displayDialog(SignInScreen.this, MMAPIConstants.DEFAULT_STRING_EMPTY, getString(R.string.pd_signing_in));
		}
	}
	
    /**
     * Function that handles the user sign in with Facebook API
     */
	private void signInFacebook() {
		if(!userPrefs.getBoolean(MMAPIConstants.SHARED_PREFS_KEY_TOS_FACEBOOK, false)) {
			launchToS(MMAPIConstants.REQUEST_CODE_TOS_FACEBOOK);
		} else {
			Session.openActiveSession(SignInScreen.this, true, new SessionStatusCallback());
		}
	}
	
    /**
     * Function that handles the user sign in with Twitter. Go to the {@link TwitterAuthScreen} and allows the user there to authenticate himself/herself call the MM SignIn with Twitter on that screen.
     * 		If user already exist in MobMonkey database, it will sign user in to the application.
     * NOTE: Not launching the browser on this screen because the app need to authenticate the user via Twitter on two different instance, SignIn and SignUp. Normal procedure requires current {@link Activity} on 
     * 		the Manifest to have launchMode 'singleTask'. This causes the {@link SignInScreen} onActivityResult callback handling to be invoked before this {@link Activity} is even created. Another problem with 
     * 		launchMode singleTask is that this {@link Activity} can only be created once, if it was destroyed and recreated, it will cause an {@link IllegalStateException} error.
     */
	private void signInTwitter() {
		if(!userPrefs.getBoolean(MMAPIConstants.SHARED_PREFS_KEY_TOS_TWITTER, false)) {
			launchToS(MMAPIConstants.REQUEST_CODE_TOS_TWITTER);
		} else {
			launchTwitterAuthScreen();
		}
	}
	
	/**
	 * Function that launches the {@link SignUpScreen} {@link Activity}
	 */
	private void signUp() {
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
    		displayAlert(R.string.ad_message_invalid_email_address);
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
    		displayAlert(R.string.ad_message_invalid_password);
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
	 * Custom {@link Session.StatusCallback} specifically for {@link SignInScreen} to handle the {@link Session} state change.
	 * @author Dezapp, LLC
	 *
	 */
	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			Log.d(TAG, TAG + "sign in with facebook");
			Log.d(TAG, TAG + "requestEmail: " + requestEmail);
			Log.d(TAG, TAG + "session opened: " + session.isOpened());
			if(session.isOpened() && requestEmail) {
	    		Session.NewPermissionsRequest request = new Session.NewPermissionsRequest(SignInScreen.this, Arrays.asList(MMAPIConstants.FACEBOOK_REQ_PERM_EMAIL));
				session.requestNewReadPermissions(request);
				Request.executeMeRequestAsync(session, new RequestGraphUserCallback());
			}
		}
	}
	
	/**
	 * Custom {@link Request.GraphUserCallback} specifically for {@link SignInScreen} to the completion of the {@link Request}.executeMeRequestAsync({@link Session}, {@link Request.GraphUserCallback}).
	 * @author Dezapp, LLC
	 *
	 */
	private class RequestGraphUserCallback implements Request.GraphUserCallback {
		@Override
		public void onCompleted(GraphUser user, Response response) {
			Log.d(TAG, TAG + "onCompleted");
			if(user != null) {
				requestEmail = false;
				//userPrefsEditor.putString("TRUE", MMAPIConstants.KEY_OAUTH_USER);
				//userPrefsEditor.commit();
				facebookUser = user;
			}
		}
	}
	
    /**
     * Custom {@link MMCallback} specifically for {@link SignInScreen} to be processed after receiving response from MobMonkey server.
     * @author Dezapp, LLC
     *
     */
	private class SignInCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
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
			}
			Log.d(TAG, TAG + "response: " + (String) obj);
		}
	}
}
