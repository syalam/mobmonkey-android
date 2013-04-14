private package com.mobmonkey.mobmonkey;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMProgressDialog;
import com.mobmonkey.mobmonkey.utils.MMUtility;
import com.mobmonkey.mobmonkeyapi.adapters.MMUserAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;
import android.text.TextUtils;

/**
 * Android {@link Activity} screen that allows user to sign up his/her account through MobMonkey, Facebook or Twitter.
 * @author Dezapp, LLC
 *
 */
public class SignUpScreen extends Activity implements OnKeyListener, OnDateChangedListener, OnTouchListener {
	private static final String TAG = "SignUpScreen: ";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	
	private InputMethodManager inputMethodManager;
	private EditText etFirstName;
	private EditText etLastName;
	private EditText etEmailAddress;
	private EditText etPassword;
	private EditText etPasswordConfirm;
	private EditText etBirthdate;
	private EditText etGender;
	private CheckBox cbAcceptedToS;
	private  MotionEvent prevEvent;
	
	private Calendar birthdate;
	
	private  boolean requestEmail;
	private GraphUser facebookUser;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, TAG + "onCreate");
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_hold);
        
        setContentView(R.layout.signup_screen);
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
        	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        }
        
        init();
    }
    
    /**
     * {@link OnKeyListener} handle when user finished entering confirmed password and go to the birthdate {@link EditText}, removes the soft keyboard
     */
    /* (non-Javadoc)
	 * @see android.view.View.OnKeyListener#onKey(android.view.View, int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
			inputMethodManager.hideSoftInputFromWindow(etPasswordConfirm.getWindowToken(), 0);
			return true;
		}
		return false;
	}

	/**
     * {@link OnTouchListener} handler for birthdate and gender {@link EditText}. When the {@link EditText}s are touched, it will prompt the user to select his/her birthdate or gender.
     */
    /*
     * (non-Javadoc)
     * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
     */
    @Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			prevEvent = event;
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP && prevEvent.getAction() == MotionEvent.ACTION_UP) {
			switch(v.getId()) {
		    	case R.id.etbirthdate:
		    		inputMethodManager.hideSoftInputFromWindow(etBirthdate.getWindowToken(), 0);
		    		promptUserBirthdate();
		    		return true;
		    	case R.id.etgender:
		    		inputMethodManager.hideSoftInputFromWindow(etGender.getWindowToken(), 0);
		    		promptUserGender();
		    		return true;
			}
		}
		
		return false;
	}

	/**
	 * Handle events when the date changes on the {@link DatePicker}
	 */
	/*
	 * (non-Javadoc)
	 * @see android.widget.DatePicker.OnDateChangedListener#onDateChanged(android.widget.DatePicker, int, int, int)
	 */
    @Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, TAG + "onActivityResult");
		
		switch(requestCode) {
			case MMAPIConstants.REQUEST_CODE_SIGN_UP_TWITTER_AUTH:
				MMProgressDialog.dismissDialog();
				
				if(resultCode == MMAPIConstants.RESULT_CODE_SUCCESS) {
					Toast.makeText(SignUpScreen.this, R.string.toast_sign_up_in_successful, Toast.LENGTH_LONG).show();
					startActivity(new Intent(SignUpScreen.this, MainScreen.class));
					finish();
				} else if(resultCode == MMAPIConstants.RESULT_CODE_NOT_FOUND) {
					Toast.makeText(SignUpScreen.this, R.string.toast_new_twitter_user, Toast.LENGTH_SHORT).show();
					Intent signUpTwitterIntent = (Intent) data.clone();
					signUpTwitterIntent.setClass(SignUpScreen.this, SignUpTwitterScreen.class);
					startActivityForResult(signUpTwitterIntent, MMAPIConstants.REQUEST_CODE_SIGN_UP_TWITTER);
				}
				break;
			case MMAPIConstants.REQUEST_CODE_SIGN_UP_TWITTER:
				Log.d(TAG, TAG + "coming back from twitter sign up");
				if(resultCode == RESULT_OK) {
					startActivity(new Intent(SignUpScreen.this, MainScreen.class));
					finish();
				}
				break;
			default:
				// TODO: Find the Facebook requestCode
				Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
				if(!requestEmail) {
					userPrefsEditor.putString(MMAPIConstants.KEY_USER, Session.getActiveSession().getAccessToken());
					userPrefsEditor.putString(MMAPIConstants.KEY_AUTH, (String) facebookUser.getProperty(MMAPIConstants.FACEBOOK_REQ_PERM_EMAIL));
					userPrefsEditor.putString(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.OAUTH_PROVIDER_FACEBOOK);
					MMUserAdapter.signUpNewUserFacebook(new SignUpCallback(), Session.getActiveSession().getAccessToken(), 
							(String) facebookUser.getProperty(MMAPIConstants.FACEBOOK_REQ_PERM_EMAIL), MMConstants.PARTNER_ID);
		    		MMProgressDialog.displayDialog(SignUpScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_signing_up_facebook));
				}
				break;
		}
	}
	
    /* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_right_out);
	}

	/**
     * Handler for when {@link Button}s or any other {@link View}s are clicked.
     * @param view {@link View} that is clicked
     */
    public void viewOnClick(View view) {
    	switch(view.getId()) {
	    	case R.id.tvtos:
	    		openToS();
	    		break;
	    	case R.id.btnsignup:
	    		signUpNormal();
	    		break;
	    	case R.id.btnsignupfacebook:
	    		signUpFacebook();
	    		break;
	    	case R.id.btnsignuptwitter:
	    		signUpTwitter();
	    		break;
    	}
    }
    
    /**
     * Initialize all the variables to be used in {@link SignUpScreen}.
     */
    private void init() {
        userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
        userPrefsEditor = userPrefs.edit();
    	
    	inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    	
    	etFirstName = (EditText) findViewById(R.id.etfirstname);
    	etLastName = (EditText) findViewById(R.id.etlastname);
    	etEmailAddress = (EditText) findViewById(R.id.etemailaddress);
    	etPassword = (EditText) findViewById(R.id.etpassword);
    	etPasswordConfirm = (EditText) findViewById(R.id.etpasswordconfirm);
    	etBirthdate = (EditText) findViewById(R.id.etbirthdate);
    	etGender = (EditText) findViewById(R.id.etgender);
    	cbAcceptedToS = (CheckBox) findViewById(R.id.cbagreetos);
    	
    	etPasswordConfirm.setOnKeyListener(SignUpScreen.this);
    	etBirthdate.setOnTouchListener(SignUpScreen.this);
    	etGender.setOnTouchListener(SignUpScreen.this);
    	
    	requestEmail = true;
    	
    	// TODO: Hardcoded values, to be removed
    	etFirstName.setText("Wilson");
    	etLastName.setText("Xie");
    	etEmailAddress.setText("Wilson@dezapp.com");
    	etPassword.setText("helloworld123");
    	etPasswordConfirm.setText("helloworld123");
    }
    
    /**
     * Function that opens the Terms of Use {@link Activity}
     */
    private void openToS() {
    	startActivity(new Intent(SignUpScreen.this, TermsofuseScreen.class));
    }
    
    /**
     * Function that handles normal user sign up with email through MobMonkey
     */
    private void signUpNormal() {
    	if(checkFirstName()) {
			userPrefsEditor.putString(MMAPIConstants.KEY_USER, etEmailAddress.getText().toString());
			userPrefsEditor.putString(MMAPIConstants.KEY_AUTH, etPassword.getText().toString());
			userPrefsEditor.putString(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.DEFAULT_STRING);
    		MMUserAdapter.signUpNewUser(new SignUpCallback(), 
    				etFirstName.getText().toString(), 
    				etLastName.getText().toString(), 
    				etEmailAddress.getText().toString(), 
    				etPassword.getText().toString(), 
    				Long.toString(birthdate.getTimeInMillis()), 
    				convertGender(),
    				cbAcceptedToS.isChecked(), 
    				MMConstants.PARTNER_ID);
    		MMProgressDialog.displayDialog(SignUpScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_signing_up));
    	}
    }
    
    /**
     * Function that handles the user sign up with Facebook API
     */
    private void signUpFacebook() {
    	if(checkAcceptedToS()) {
    		Session.openActiveSession(SignUpScreen.this, true, new SessionStatusCallback());
    		userPrefsEditor.putBoolean(MMAPIConstants.SHARED_PREFS_KEY_TOS_FACEBOOK, true);
    		userPrefsEditor.commit();
    	}
    }

    /**
     * Function that handles the user sign up with Twitter. Go to the {@link TwitterAuthScreen} and allows the user there to authenticate himself/herself call the MM SignUp with Twitter on that screen.
     * 		If user already exist in MobMonkey database, it will sign user in to the application. If not, it will come back to this screen and be transported to the {@link SignUpTwitterScreen} for user to enter
     * 		his or her information.
     * NOTE: Not launching the browser on this screen because the app need to authenticate the user via Twitter on two different instance, SignIn and SignUp. Normal procedure requires current {@link Activity} on 
     * 		the Manifest to have launchMode 'singleTask'. This causes the {@link SignInScreen} onActivityResult callback handling to be invoked before this {@link Activity} is even created. Another problem with 
     * 		launchMode singleTask is that this {@link Activity} can only be created once, if it was destroyed and recreated, it will cause an {@link IllegalStateException} error.
     */
    private void signUpTwitter() {    	
    	if(checkAcceptedToS()) {
    		userPrefsEditor.putBoolean(MMAPIConstants.SHARED_PREFS_KEY_TOS_TWITTER, true);
    		userPrefsEditor.commit();
    		MMProgressDialog.displayDialog(SignUpScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_launch_twitter_auth_screen));
    		Intent twitterAuthIntent = new Intent(SignUpScreen.this, TwitterAuthScreen.class);
    		twitterAuthIntent.putExtra(MMAPIConstants.REQUEST_CODE, MMAPIConstants.REQUEST_CODE_SIGN_UP_TWITTER_AUTH);
    		startActivityForResult(twitterAuthIntent, MMAPIConstants.REQUEST_CODE_SIGN_UP_TWITTER_AUTH);
    	}
    }

	/**
     * Function that check if the first name {@link EditText} field is valid and is not empty and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkFirstName() {
    	if(!TextUtils.isEmpty(etFirstName.getText())) {
    		return checkLastName();
    	} else {
    		displayAlert(R.string.ad_message_invalid_first_name);
    		return false;
    	}
    }
    
    /**
     * Function that check if the last name {@link EditText} field is valid and is not empty and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkLastName() {
    	if(!TextUtils.isEmpty(etLastName.getText().toString())) {
    		return checkEmailAddress();
    	} else {
    		displayAlert(R.string.ad_message_invalid_last_name);
    		return false;
    	}
    }
    
    /**
     * Function that check if the email address {@link EditText} field is valid and is not empty and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkEmailAddress() {
    	if(!TextUtils.isEmpty(etEmailAddress.getText())) {
    		userPrefsEditor.putString(MMAPIConstants.KEY_EMAIL_ADDRESS, etEmailAddress.getText().toString());
    		userPrefsEditor.commit();
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
    	if(!TextUtils.isEmpty(etPassword.getText()) && !TextUtils.isEmpty(etPasswordConfirm.getText())) {
    		if(etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())) {
    			userPrefsEditor.putString(MMAPIConstants.KEY_PASSWORD, etPassword.getText().toString());
    			userPrefsEditor.commit();
    			return checkBirthdate();
    		} else {
    			displayAlert(R.string.ad_message_invalid_password_not_match);
    			return false;
    		}
    	} else {
    		displayAlert(R.string.ad_message_invalid_password);
    		return false;
    	}
    }
    
    /**
     * Function that check if the birthdate {@link EditText} field is valid and is not empty and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkBirthdate() {
    	if(!TextUtils.isEmpty(etBirthdate.getText())) {
    		return checkGender();
    	} else {
    		displayAlert(R.string.ad_message_invalid_birthdate);
    		return false;
    	}
    }
    
    /**
     * Function that check if the gender {@link EditText} field is valid and is not empty and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkGender() {
    	if(!TextUtils.isEmpty(etGender.getText())) {
    		return checkAcceptedToS();
    	} else {
    		displayAlert(R.string.ad_message_invalid_gender);
    		return false;
    	}
    }
    
    /**
     * Function that check if the {@link CheckBox} accept term of use is checked and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkAcceptedToS() {
    	if(cbAcceptedToS.isChecked()) {
    		return true;
    	} else {
    		displayAlert(R.string.ad_message_invalid_tos);
    		return false;
    	}
    }
    
    /**
     * Function that converts the gender of the user from {@link String} representation to {@link Integer} representation.
     * @return
     */
    private int convertGender() {
    	int gender = MMAPIConstants.DEFAULT_INT;
    	if(etGender.getText().toString().equalsIgnoreCase(MMAPIConstants.TEXT_MALE)) {
    		gender = MMAPIConstants.NUM_MALE;
    	} else if(etGender.getText().toString().equalsIgnoreCase(MMAPIConstants.TEXT_FEMALE)) {
    		gender = MMAPIConstants.NUM_FEMALE;
    	}
    	return gender;
    }
    
    /**
     * Display an {@link AlertDialog} with the associated message informing user that they forgot enter a certain input field.
     * @param messageId String resource id of the message to be displayed
     */
    private void displayAlert(int messageId) {
    	new AlertDialog.Builder(SignUpScreen.this)
    		.setTitle(R.string.app_name)
    		.setMessage(messageId)
    		.setNeutralButton(android.R.string.ok, null)
    		.show();
    }
    
    /**
     * Prompt the user with an {@link AlertDialog} to select his/her birthdate.
     */
    private void promptUserBirthdate() {
    	LayoutInflater layoutInflator = LayoutInflater.from(SignUpScreen.this);
    	View vBirthdate = layoutInflator.inflate(R.layout.birthdate_picker, null);
    	
    	final DatePicker dpBirthdate = (DatePicker) vBirthdate.findViewById(R.id.dpbirthdate);
    	
    	if(birthdate == null) {
	    	birthdate = Calendar.getInstance();
	    	birthdate.setTimeInMillis(System.currentTimeMillis());
	    	birthdate.set(birthdate.get(Calendar.YEAR) - 21, birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH));
		    dpBirthdate.init(birthdate.get(Calendar.YEAR), birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH), SignUpScreen.this);
    	} else {
    		dpBirthdate.init(birthdate.get(Calendar.YEAR), birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH), SignUpScreen.this);
    	}
    	
    	new AlertDialog.Builder(SignUpScreen.this)
    		.setTitle(R.string.ad_title_birthdate)
    		.setView(vBirthdate)
    		.setCancelable(false)
    		.setPositiveButton(R.string.ad_btn_choose, new DialogInterface.OnClickListener() {
    			@Override
				public void onClick(DialogInterface dialog, int which) {
					birthdate.set(dpBirthdate.getYear(), dpBirthdate.getMonth(), dpBirthdate.getDayOfMonth());
					etBirthdate.setText(MMUtility.getDate(birthdate.getTimeInMillis(), "MMM dd, yyyy"));
				}
			})
			.setNegativeButton(R.string.ad_btn_cancel, null)
			.show();
    }
    
    /**
     * Prompt the user with an {@link AlertDialog} for his/her gender.
     */
    private void promptUserGender() {
    	new AlertDialog.Builder(SignUpScreen.this)
    		.setTitle(R.string.ad_title_gender)
    		.setItems(R.array.ad_list_gender, new DialogInterface.OnClickListener() {
    			@Override
				public void onClick(DialogInterface dialog, int which) {
					etGender.setText(getResources().getStringArray(R.array.ad_list_gender)[which]);
				}
			})
    		.setNegativeButton(R.string.ad_btn_cancel, null)
    		.setCancelable(false)
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
	    		Session.NewPermissionsRequest request = new Session.NewPermissionsRequest(SignUpScreen.this, Arrays.asList(MMAPIConstants.FACEBOOK_REQ_PERM_EMAIL));
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
				facebookUser = user;
			}
		}
	}
    
    /**
     * Custom {@link MMCallback} specifically for {@link SignUpScreen} to be processed after receiving response from MobMonkey server.
     * @author Dezapp, LLC
     *
     */
    private class SignUpCallback implements MMCallback {
    	@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				try {
					JSONObject response = new JSONObject((String) obj);
					if(response.getString(MMAPIConstants.KEY_RESPONSE_STATUS).equals(MMAPIConstants.RESPONSE_STATUS_SUCCESS)) {
						Toast.makeText(SignUpScreen.this, R.string.toast_sign_up_successful, Toast.LENGTH_SHORT).show();
						userPrefsEditor.commit();
						startActivity(new Intent(SignUpScreen.this, MainScreen.class));
						finish();
						overridePendingTransition(R.anim.slide_hold, R.anim.slide_right_out);
					} else {
						Toast.makeText(SignUpScreen.this, response.getString(MMAPIConstants.KEY_RESPONSE_DESC), Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			Log.d(TAG, TAG + "response: " + (String) obj);
		}	
    }
}
