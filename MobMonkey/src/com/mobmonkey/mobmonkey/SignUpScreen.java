package com.mobmonkey.mobmonkey;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
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
 * Android activity screen that allows user to sign up his/her account through MobMonkey, Facebook or twitter.
 * @author Dezapp, LLC
 *
 */
public class SignUpScreen extends Activity implements OnDateChangedListener, OnTouchListener {
	private static final String TAG = "SignUpScreen: ";
	SharedPreferences userPrefs;
	SharedPreferences.Editor userPrefsEditor;
	
	HashMap<String,Object> userInfo;
	
	InputMethodManager imm;
	ProgressDialog progressDialog;
	EditText etFirstName;
	EditText etLastName;
	EditText etEmailAddress;
	EditText etPassword;
	EditText etPasswordConfirm;
	EditText etBirthdate;
	EditText etGender;
	CheckBox cbAcceptedToS;
    MotionEvent prevEvent;
	
	Calendar birthdate;
	
	String userEmail;
	
	LoginActivity fbLogin;
	Twitter twitter;
	RequestToken requestToken;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.signupscreen);
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
        	StrictMode.ThreadPolicy policy = 
        		new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        }
        
        initUserInfoFields();
        
        userPrefs = getSharedPreferences("USER_PREFS", MODE_PRIVATE);
        userPrefsEditor = userPrefs.edit();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    /**
     * Handler for when {@link Button}s or any other {@link View}s are clicked.
     * @param view
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
    private void initUserInfoFields() {
    	userInfo = new HashMap<String, Object>();
    	
    	imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    	
    	etFirstName = (EditText) findViewById(R.id.etfirstname);
    	etLastName = (EditText) findViewById(R.id.etlastname);
    	etEmailAddress = (EditText) findViewById(R.id.etemailaddress);
    	etPassword = (EditText) findViewById(R.id.etpassword);
    	etPasswordConfirm = (EditText) findViewById(R.id.etpasswordconfirm);
    	etBirthdate = (EditText) findViewById(R.id.etbirthdate);
    	etGender = (EditText) findViewById(R.id.etgender);
    	cbAcceptedToS = (CheckBox) findViewById(R.id.cbagreetos);
    	
    	etBirthdate.setOnTouchListener(SignUpScreen.this);
    	etGender.setOnTouchListener(SignUpScreen.this);
    	
    	// TODO: Hardcoded values, to be removed
    	etFirstName.setText("Wilson");
    	etLastName.setText("Xie");
    	etEmailAddress.setText("Wilson@dezapp.com");
    	etPassword.setText("helloworld123");
    	etPasswordConfirm.setText("helloworld123");
    }
    
    /**
     * 
     */
    private void openToS() {
    	startActivity(new Intent(SignUpScreen.this, TermsofuseScreen.class));
    }
    
    /**
     * Function that handles user sign up through MobMonkey
     */
    private void signUpNormal() {
    	if(checkFirstName()) {
    		MMSignUpAdapter.signUpNewUser(new SignUpCallback(), userInfo, MMConstants.PARTNER_ID);
    		progressDialog = ProgressDialog.show(SignUpScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_signing_up), true, false);
    	}
    }
    
    boolean requestEmail = true;
    private void signUpFacebook() {
    	if(checkAcceptedToS()) {
//    		Session session = Session.getActiveSession();
//    		if(session == null) {
//    			session = new Session(this);
//    		}
//    		Session.setActiveSession(session);
//    		Session.OpenRequest request = new Session.OpenRequest(this).setCallback(null);
//    		request.setPermissions(Arrays.asList("email"));
    		
//    		Session.NewPermissionsRequest request = new Session.NewPermissionsRequest(SignUpScreen.this, Arrays.asList("email"));
//    		Session session = Session.getActiveSession();
//    		
//    		if(session == null) {
//    			session = new Session(this);
//    		}
//    		
//    		session.requestNewReadPermissions(request);
//    		Session.setActiveSession(session);
////    		session.openForRead(new Session.OpenRequest(this).setCallback(new Session.StatusCallback() {
////				public void call(Session session, SessionState state, Exception exception) {
////					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
////						public void onCompleted(GraphUser user, Response response) {
////							if(user != null) {
////								Log.d(TAG, TAG + "user: " + user.getUsername());
////							}
////						}
////					});
////				}
////			}));
    		
    		Session.openActiveSession(SignUpScreen.this, true, new Session.StatusCallback() {
				public void call(Session session, SessionState state, Exception exception) {
					if(session.isOpened() && requestEmail) {
			    		Session.NewPermissionsRequest request = new Session.NewPermissionsRequest(SignUpScreen.this, Arrays.asList("email"));
						session.requestNewReadPermissions(request);
						requestEmail = false;
						Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
							public void onCompleted(GraphUser user, Response response) {
								if(user != null) {
									Log.d(TAG, TAG + "graphUser: " + user.getUsername());
									Log.d(TAG, TAG + "user: " + user.getProperty("email"));
									userPrefsEditor.putString("FBToken", Session.getActiveSession().getAccessToken());
									userPrefsEditor.putString("FBUserName", (String) user.getProperty("email"));
									userPrefsEditor.commit();
//									userEmail = (String) user.getProperty("email");
								}
							}
						});
					}
				}
			});
    	}
    }
    
    private void signUpTwitter() {
    	String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    	String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    	String PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn";
    	
    	String TWITTER_CALLBACK_URL = "mobmonkey://com.mobmonkey.mobmonkey?";
    	
    	String URL_TWITTER_AUTH = "auth_url";
        String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
        String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
    	
    	if(checkAcceptedToS()) {
    		ConfigurationBuilder builder = new ConfigurationBuilder();
    		builder.setOAuthConsumerKey(MMConstants.TWITTER_CONSUMER_KEY);
    		builder.setOAuthConsumerSecret(MMConstants.TWITTER_CONSUMER_SECRET);
    		builder.setOAuthAccessToken(userPrefs.getString("twitter_access_token", null));
    		builder.setOAuthAccessTokenSecret(userPrefs.getString("twitter_access_token_secret", null));
    		Configuration configuration = builder.build();
    		
    		TwitterFactory factory = new TwitterFactory(configuration);
    		twitter = factory.getInstance();
    		
    		try {
				requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
				Log.d(TAG, TAG + "authURL: " + requestToken.getAuthenticationURL());
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
			} catch (TwitterException e) {
				e.printStackTrace();
			}
    	}
    }
    
    /**
     * Handle the callback from authenticating user Twitter
     * Process - First sign user in to MobMonkey via Twitter, if success, go to user Settings screen, else go to RegisterUserTwitterDetails screen
     */
	/* (non-Javadoc)
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(TAG, TAG + "onNewIntent");
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		
		userPrefsEditor.putString(MMAPIConstants.KEY_OAUTH_TOKEN, uri.getQueryParameter("oauth_token"));
		userPrefsEditor.commit();

//		Log.d(TAG, TAG + "twitter: " + twitter.get)
		MMSignInAdapter.signInUserTwitter(new SignUpCallback(), userPrefs.getString(MMAPIConstants.KEY_OAUTH_TOKEN, MMAPIConstants.DEFAULT_STRING), "@scumbaghank2", MMConstants.PARTNER_ID);
	}

	/**
     * Function that check if the first name {@link EditText} field is valid and is not empty and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkFirstName() {
    	if(!TextUtils.isEmpty(etFirstName.getText())) {
    		userInfo.put(MMAPIConstants.KEY_FIRST_NAME, etFirstName.getText().toString());
    		return checkLastName();
    	} else {
    		displayAlert(R.string.alert_invalid_first_name);
    		return false;
    	}
    }
    
    /**
     * Function that check if the last name {@link EditText} field is valid and is not empty and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkLastName() {
    	if(!TextUtils.isEmpty(etLastName.getText().toString())) {
    		userInfo.put(MMAPIConstants.KEY_LAST_NAME, etLastName.getText().toString());
    		return checkEmailAddress();
    	} else {
    		displayAlert(R.string.alert_invalid_last_name);
    		return false;
    	}
    }
    
    /**
     * Function that check if the email address {@link EditText} field is valid and is not empty and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkEmailAddress() {
    	if(!TextUtils.isEmpty(etEmailAddress.getText())) {
    		userInfo.put(MMAPIConstants.KEY_EMAIL_ADDRESS, etEmailAddress.getText().toString());
    		userPrefsEditor.putString(MMAPIConstants.KEY_EMAIL_ADDRESS, etEmailAddress.getText().toString());
    		userPrefsEditor.commit();
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
    	if(!TextUtils.isEmpty(etPassword.getText()) && !TextUtils.isEmpty(etPasswordConfirm.getText())) {
    		if(etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())) {
    			userInfo.put(MMAPIConstants.KEY_PASSWORD, etPassword.getText().toString());
    			userPrefsEditor.putString(MMAPIConstants.KEY_PASSWORD, etPassword.getText().toString());
    			userPrefsEditor.commit();
    			return checkBirthdate();
    		} else {
    			displayAlert(R.string.alert_invalid_password_not_match);
    			return false;
    		}
    	} else {
    		displayAlert(R.string.alert_invalid_password);
    		return false;
    	}
    }
    
    /**
     * Function that check if the birthdate {@link EditText} field is valid and is not empty and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkBirthdate() {
    	if(!TextUtils.isEmpty(etBirthdate.getText())) {
    		userInfo.put(MMAPIConstants.KEY_BIRTHDATE, birthdate.getTimeInMillis());
    		return checkGender();
    	} else {
    		displayAlert(R.string.alert_invalid_birthdate);
    		return false;
    	}
    }
    
    /**
     * Function that check if the gender {@link EditText} field is valid and is not empty and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkGender() {
    	if(!TextUtils.isEmpty(etGender.getText())) {
    		userInfo.put(MMAPIConstants.KEY_GENDER, convertGender());
    		return checkAcceptedToS();
    	} else {
    		displayAlert(R.string.alert_invalid_gender);
    		return false;
    	}
    }
    
    /**
     * Function that check if the {@link CheckBox} accept term of use is checked and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkAcceptedToS() {
    	if(cbAcceptedToS.isChecked()) {
    		userInfo.put(MMAPIConstants.KEY_ACCEPTEDTOS, cbAcceptedToS.isChecked());
    		return true;
    	} else {
    		displayAlert(R.string.alert_invalid_tos);
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
    		.setTitle(R.string.title_birthdate)
    		.setView(vBirthdate)
    		.setCancelable(false)
    		.setPositiveButton(R.string.btn_choose, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					birthdate.set(dpBirthdate.getYear(), dpBirthdate.getMonth(), dpBirthdate.getDayOfMonth());
					Date tempDate = new Date(birthdate.get(Calendar.YEAR) - 1900, birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH));
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
					etBirthdate.setText(simpleDateFormat.format(tempDate));
				}
			})
			.setNegativeButton(R.string.btn_cancel, null)
			.show();
    }
    
    /**
     * Prompt the user with an {@link AlertDialog} for his/her gender.
     */
    private void promptUserGender() {
    	new AlertDialog.Builder(SignUpScreen.this)
    		.setTitle(R.string.title_gender)
    		.setItems(R.array.alert_gender, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					etGender.setText(getResources().getStringArray(R.array.alert_gender)[which]);
				}
			})
    		.setNegativeButton(R.string.btn_cancel, null)
    		.setCancelable(false)
    		.show();
    }
    
    /**
     * Custom {@link MMCallback} specifically for SignUpScreen to be processed after receiving response from server.
     * @author Dezapp, LLC
     *
     */
    private class SignUpCallback implements MMCallback {
		public void processCallback(Object obj) {
			if(progressDialog != null) {
				progressDialog.dismiss();
			}
			
			try {
				JSONObject response = new JSONObject((String) obj);
				if(response.getString("status").equals("Success")) {
					Toast.makeText(SignUpScreen.this, R.string.toast_sign_up_successful, Toast.LENGTH_SHORT).show();
					startActivity(new Intent(SignUpScreen.this, MainScreen.class));
					finish();
				} else if (response.getString("status").equals("Failure") && response.getString("id").equals("404")){
					Intent signUpTwitterIntent = new Intent(SignUpScreen.this, SignUpTwitter.class);
					startActivityForResult(signUpTwitterIntent, 1000);
				} else {
					// TODO: alert user signup failed
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d(TAG, TAG + "response: " + (String) obj);
		}	
    }
    
    /**
     * {@link OnTouchListener} handler for birthdate and gender {@link EditText}. When the {@link EditText}s are touched, it will prompt the user to select his/her birthdate or gender.
     */
    /*
     * (non-Javadoc)
     * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
     */
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			prevEvent = event;
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP) {
			switch(v.getId()) {
		    	case R.id.etbirthdate:
		    		imm.hideSoftInputFromWindow(etBirthdate.getWindowToken(), 0);
		    		promptUserBirthdate();
		    		return true;
		    	case R.id.etgender:
		    		imm.hideSoftInputFromWindow(etGender.getWindowToken(), 0);
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
	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1000) {
			Log.d(TAG, TAG + "coming back from twitter sign up");
		} else {
			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		}
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
		MMSignUpAdapter.signUpNewUserFacebook(new SignUpCallback(), userPrefs.getString("FBToken", ""), userPrefs.getString("FBUserName", ""), MMConstants.PARTNER_ID);
	}
}
