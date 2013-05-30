package com.mobmonkey.mobmonkeyandroid;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMDeviceUUID;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
	private InputMethodManager inputMethodManager;

	private EditText etEmailAddress;
	private EditText etPassword;
	
	private boolean requestEmail;
	private GraphUser facebookUser;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: check if this is still needed...
        if (android.os.Build.VERSION.SDK_INT > 9) {
        	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        }
		
		super.onCreate(savedInstanceState);
        MMDeviceUUID.setContext(getApplicationContext());
		
        overridePendingTransition(R.anim.slide_hold, R.anim.slide_hold);
        
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
			case MMSDKConstants.REQUEST_CODE_TOS_FACEBOOK:
				if(resultCode == RESULT_OK) {
					Session.openActiveSession(SignInScreen.this, true, new SessionStatusCallback());
				}
				break;
			case MMSDKConstants.REQUEST_CODE_TOS_TWITTER:
				if(resultCode == RESULT_OK) {
					launchTwitterAuthScreen();
				}
				break;
			case MMSDKConstants.REQUEST_CODE_SIGN_IN_TWITTER_AUTH:
				MMProgressDialog.dismissDialog();
				
				if(resultCode == MMSDKConstants.RESULT_CODE_SUCCESS) {
					Toast.makeText(SignInScreen.this, R.string.toast_sign_in_successful, Toast.LENGTH_SHORT).show();
					startActivity(new Intent(SignInScreen.this, MainScreen.class));
				} else if(resultCode == MMSDKConstants.RESULT_CODE_NOT_FOUND) {
					Toast.makeText(SignInScreen.this, R.string.toast_new_twitter_user, Toast.LENGTH_SHORT).show();
					Intent signUpTwitterIntent = (Intent) data.clone();
					signUpTwitterIntent.setClass(SignInScreen.this, SignUpTwitterScreen.class);
					startActivityForResult(signUpTwitterIntent, MMSDKConstants.REQUEST_CODE_SIGN_UP_TWITTER);
				}
				break;
			case MMSDKConstants.REQUEST_CODE_SIGN_UP_TWITTER:
				if(resultCode == RESULT_OK) {
					startActivity(new Intent(SignInScreen.this, MainScreen.class));
				}
				break;
			default:
				Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
				if(!requestEmail) {
//					userPrefsEditor.putString(MMSDKConstants.KEY_USER, (String) facebookUser.getProperty(MMSDKConstants.FACEBOOK_REQ_PERM_EMAIL));
//					userPrefsEditor.putString(MMSDKConstants.KEY_AUTH, 	Session.getActiveSession().getAccessToken());
					String emailAddress = (String) facebookUser.getProperty(MMSDKConstants.FACEBOOK_REQ_PERM_EMAIL);
					Log.d(TAG, TAG + "Email address: " + emailAddress);
					if(emailAddress == null) {
						requestEmail = true;
						Session.openActiveSession(SignInScreen.this, true, new SessionStatusCallback());
					} else {
						userPrefsEditor.putBoolean(MMSDKConstants.KEY_USE_OAUTH, true);
						userPrefsEditor.putString(MMSDKConstants.KEY_OAUTH_PROVIDER, MMSDKConstants.OAUTH_PROVIDER_FACEBOOK);
						userPrefsEditor.putString(MMSDKConstants.KEY_OAUTH_PROVIDER_USER_NAME, emailAddress);
						MMUserAdapter.signInUserFacebook(new SignInCallback(),
														 MMConstants.PARTNER_ID,
														 Session.getActiveSession().getAccessToken(),
														 emailAddress,
														 facebookUser.getFirstName(),
														 facebookUser.getLastName(),
														 convertBirthdate(facebookUser.getBirthday()),
														 convertGender((String) facebookUser.getProperty(MMSDKConstants.FACEBOOK_REQ_PERM_GENDER)));
						MMProgressDialog.displayDialog(SignInScreen.this,
													   MMSDKConstants.DEFAULT_STRING_EMPTY,
													   getString(R.string.pd_signing_in_facebook));
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
	 * Initialize all the variables to be used in {@link SignInScreen}
	 */
	private void init() {
		userPrefs = getSharedPreferences(MMSDKConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
    	inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		etEmailAddress = (EditText) findViewById(R.id.etemailaddress);
		etPassword = (EditText) findViewById(R.id.etpassword);
        
		requestEmail = true;
	}
	
	private void launchToS(int requestCode) {
		Intent openToSIntent = new Intent(SignInScreen.this, TermsofuseScreen.class);
		openToSIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_TOS_DISPLAY_BUTTON, true);
		openToSIntent.putExtra(MMSDKConstants.REQUEST_CODE, requestCode);
		startActivityForResult(openToSIntent, requestCode);
	}
	
	private void launchTwitterAuthScreen() {
		MMProgressDialog.displayDialog(SignInScreen.this,
									   MMSDKConstants.DEFAULT_STRING_EMPTY,
									   getString(R.string.pd_launch_twitter_auth_screen));
		Intent twitterAuthIntent = new Intent(SignInScreen.this, TwitterAuthScreen.class);
		twitterAuthIntent.putExtra(MMSDKConstants.REQUEST_CODE, MMSDKConstants.REQUEST_CODE_SIGN_IN_TWITTER_AUTH);
		startActivityForResult(twitterAuthIntent, MMSDKConstants.REQUEST_CODE_SIGN_IN_TWITTER_AUTH);
	}
	
	/**
	 * Functional that handles the normal user sign in with email through MobMonkey
	 */
	private void signInNormal() {
		if(checkEmailAddress()) {
			userPrefsEditor.putString(MMSDKConstants.KEY_USER, etEmailAddress.getText().toString());
			userPrefsEditor.putString(MMSDKConstants.KEY_AUTH, etPassword.getText().toString());
			userPrefsEditor.putBoolean(MMSDKConstants.KEY_USE_OAUTH, false);
//			userPrefsEditor.putString(MMSDKConstants.KEY_OAUTH_PROVIDER, MMSDKConstants.DEFAULT_STRING_EMPTY);
			MMUserAdapter.signInUser(new SignInCallback(),
									 MMConstants.PARTNER_ID,
									 etEmailAddress.getText().toString(),
									 etPassword.getText().toString());
    		MMProgressDialog.displayDialog(SignInScreen.this,
    									   MMSDKConstants.DEFAULT_STRING_EMPTY,
    									   getString(R.string.pd_signing_in));
		}
	}
	
    /**
     * Function that handles the user sign in with Facebook API
     */
	private void signInFacebook() {
		launchToS(MMSDKConstants.REQUEST_CODE_TOS_FACEBOOK);
	}
	
    /**
     * Function that handles the user sign in with Twitter. Go to the {@link TwitterAuthScreen} and allows the user there to authenticate himself/herself call the MM SignIn with Twitter on that screen.
     * 		If user already exist in MobMonkey database, it will sign user in to the application.
     * NOTE: Not launching the browser on this screen because the app need to authenticate the user via Twitter on two different instance, SignIn and SignUp. Normal procedure requires current {@link Activity} on 
     * 		the Manifest to have launchMode 'singleTask'. This causes the {@link SignInScreen} onActivityResult callback handling to be invoked before this {@link Activity} is even created. Another problem with 
     * 		launchMode singleTask is that this {@link Activity} can only be created once, if it was destroyed and recreated, it will cause an {@link IllegalStateException} error.
     */
	private void signInTwitter() {
		launchTwitterAuthScreen();
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
	 * 
	 * @param date
	 * @return
	 */
	private String convertBirthdate(String date) {
		try {
			Date birthdate = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(date);
			return Long.toString(birthdate.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
    /**
     * Function that converts the gender of the user from {@link String} representation to {@link Integer} representation.
     * @return
     */
    private String convertGender(String sex) {
    	int gender = MMSDKConstants.DEFAULT_INT;
    	if(sex.equalsIgnoreCase(MMSDKConstants.TEXT_MALE)) {
    		gender = MMSDKConstants.NUM_MALE;
    	} else if(sex.equalsIgnoreCase(MMSDKConstants.TEXT_FEMALE)) {
    		gender = MMSDKConstants.NUM_FEMALE;
    	}
    	return Integer.toString(gender);
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
	    		Session.NewPermissionsRequest request = new Session.NewPermissionsRequest(SignInScreen.this, Arrays.asList(MMSDKConstants.FACEBOOK_REQ_PERM_EMAIL, MMSDKConstants.FACEBOOK_REQ_PERM_BIRTHDAY));
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
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(SignInScreen.this, getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject response = new JSONObject((String) obj);
						if(response.getString(MMSDKConstants.JSON_KEY_ID).equals(MMSDKConstants.RESPONSE_ID_SUCCESS)) {
							inputMethodManager.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
							Toast.makeText(SignInScreen.this, R.string.toast_sign_in_successful, Toast.LENGTH_SHORT).show();
							if(requestEmail == false) {
								requestEmail = true;
							}
							userPrefsEditor.commit();
							startActivity(new Intent(SignInScreen.this, MainScreen.class));
						} else {
							Toast.makeText(SignInScreen.this, response.getString(MMSDKConstants.JSON_KEY_DESCRIPTION), Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			Log.d(TAG, TAG + "response: " + (String) obj);
		}
	}
}
