package com.mobmonkey.mobmonkeyandroid;

import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMUserAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;

/**
 * Android {@link Activity} screen that allows the user to sign up with Twitter with Twitter credentials and entered information
 * @author Dezapp, LLC
 *
 */
public class SignUpTwitterScreen extends Activity implements OnKeyListener, OnTouchListener, OnDateChangedListener {
	private static final String TAG = "SignUpTwitter: ";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	
	private String providerUserName;
	
	private MotionEvent prevEvent;
	private InputMethodManager inputMethodManager;
	
	private TextView tvProviderUserName;
	private EditText etFirstName;
	private EditText etLastName;
	private EditText etEmailAddress;
	private EditText etBirthdate;
	private EditText etGender;
	
	private Calendar birthdate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_hold);
		setContentView(R.layout.signup_twitter_screen);
		
		init();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Log.d(TAG, TAG + "onBackPressed");
		setResult(Activity.RESULT_CANCELED);
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_right_out);
	}

    /**
     * {@link OnKeyListener} handle when user finished entering email address and go to the birthdate {@link EditText}, removes the soft keyboard
     */
	/* (non-Javadoc)
	 * @see android.view.View.OnKeyListener#onKey(android.view.View, int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
			inputMethodManager.hideSoftInputFromWindow(etEmailAddress.getWindowToken(), 0);
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
		    		inputMethodManager.hideSoftInputFromWindow(etGender.getWindowToken(), 0);
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
	
    /**
     * Handler for when {@link Button}s or any other {@link View}s are clicked.
     * @param view {@link View} that is clicked
     */
	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btnsignup:
				signUpTwitter();
				break;
		}
	}
	
    /**
     * Initialize all the variables to be used in {@link SignUpTwitterScreen}.
     */
	private void init() {
    	userPrefs = getSharedPreferences(MMSDKConstants.USER_PREFS, MODE_PRIVATE);
    	userPrefsEditor = userPrefs.edit();
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		providerUserName = getIntent().getStringExtra(MMSDKConstants.KEY_OAUTH_PROVIDER_USER_NAME);
    	
    	tvProviderUserName = (TextView) findViewById(R.id.tvproviderusername);
    	etFirstName = (EditText) findViewById(R.id.etfirstname);
    	etLastName = (EditText) findViewById(R.id.etlastname);
    	etEmailAddress = (EditText) findViewById(R.id.etemailaddress);
    	etBirthdate = (EditText) findViewById(R.id.etbirthdate);
    	etGender = (EditText) findViewById(R.id.etgender);
    	
    	tvProviderUserName.setText("@" + providerUserName + " user info");
    	etEmailAddress.setOnKeyListener(SignUpTwitterScreen.this);
    	etBirthdate.setOnTouchListener(SignUpTwitterScreen.this);
    	etGender.setOnTouchListener(SignUpTwitterScreen.this);
	}
	
    /**
     * Prompt the user with an {@link AlertDialog} to select his/her birthdate.
     */
    private void promptUserBirthdate() {
    	LayoutInflater layoutInflator = LayoutInflater.from(SignUpTwitterScreen.this);
    	View vBirthdate = layoutInflator.inflate(R.layout.birthdate_picker, null);
    	
    	final DatePicker dpBirthdate = (DatePicker) vBirthdate.findViewById(R.id.dpbirthdate);
    	
    	if(birthdate == null) {
	    	birthdate = Calendar.getInstance();
	    	birthdate.setTimeInMillis(System.currentTimeMillis());
	    	birthdate.set(birthdate.get(Calendar.YEAR) - 21, birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH));
		    dpBirthdate.init(birthdate.get(Calendar.YEAR), birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH), SignUpTwitterScreen.this);
    	} else {
    		dpBirthdate.init(birthdate.get(Calendar.YEAR), birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH), SignUpTwitterScreen.this);
    	}
    	
    	new AlertDialog.Builder(SignUpTwitterScreen.this)
    		.setTitle(R.string.ad_title_birthdate)
    		.setView(vBirthdate)
    		.setCancelable(false)
    		.setPositiveButton(R.string.ad_btn_choose, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					birthdate.set(dpBirthdate.getYear(), dpBirthdate.getMonth(), dpBirthdate.getDayOfMonth());
					etBirthdate.setText(MMUtility.getDate(birthdate.getTimeInMillis(), MMSDKConstants.DATE_FORMAT_MMM_DD_COMMA_YYYY));
				}
			})
			.setNegativeButton(R.string.ad_btn_cancel, null)
			.show();
    }
    
    /**
     * Prompt the user with an {@link AlertDialog} for his/her gender.
     */
    private void promptUserGender() {
    	new AlertDialog.Builder(SignUpTwitterScreen.this)
    		.setTitle(R.string.ad_title_gender)
    		.setItems(R.array.ad_list_gender, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					etGender.setText(getResources().getStringArray(R.array.ad_list_gender)[which]);
				}
			})
    		.setNegativeButton(R.string.ad_btn_cancel, null)
    		.setCancelable(false)
    		.show();
    }

    /**
     * Function that handles the user sign up with Twitter with Twitter credentials and user entered information
     */
    private void signUpTwitter() {
    	if(checkFirstName()) {
    		MMUserAdapter.signUpNewUserTwitter(new SignUpTwitterCallback(), 
    										   etFirstName.getText().toString(),
    										   etLastName.getText().toString(),
    										   getIntent().getStringExtra(MMSDKConstants.KEY_OAUTH_TOKEN),
    										   providerUserName,
    										   etEmailAddress.getText().toString(),
    										   Long.toString(birthdate.getTimeInMillis()),
    										   convertGender(),
    										   MMConstants.PARTNER_ID);
    		MMProgressDialog.displayDialog(SignUpTwitterScreen.this,
    									   MMSDKConstants.DEFAULT_STRING_EMPTY,
    									   getString(R.string.pd_signing_up));
    	}
    }
    
	/**
     * Function that check if the first name {@link EditText} field is valid and is not empty and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkFirstName() {
    	if(!TextUtils.isEmpty(etFirstName.getText())) {
//    		userInfo.put(MMSDKConstants.KEY_FIRST_NAME, etFirstName.getText().toString());
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
//    		userInfo.put(MMSDKConstants.KEY_LAST_NAME, etLastName.getText().toString());
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
//    		userInfo.put(MMSDKConstants.KEY_EMAIL_ADDRESS, etEmailAddress.getText().toString());
//    		userPrefsEditor.putString(MMSDKConstants.KEY_EMAIL_ADDRESS, etEmailAddress.getText().toString());
//    		userPrefsEditor.commit();
    		return checkBirthdate();
    	} else {
    		displayAlert(R.string.ad_message_invalid_email_address);
    		return false;
    	}
    }
    
    /**
     * Function that check if the birthdate {@link EditText} field is valid and is not empty and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkBirthdate() {
    	if(!TextUtils.isEmpty(etBirthdate.getText())) {
//    		userInfo.put(MMSDKConstants.KEY_BIRTHDATE, birthdate.getTimeInMillis());
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
//    		userInfo.put(MMSDKConstants.KEY_GENDER, convertGender());
    		return true;
    	} else {
    		displayAlert(R.string.ad_message_invalid_gender);
    		return false;
    	}
    }
    
    /**
     * Function that converts the gender of the user from {@link String} representation to {@link Integer} representation.
     * @return
     */
    private int convertGender() {
    	int gender = MMSDKConstants.DEFAULT_INT;
    	if(etGender.getText().toString().equalsIgnoreCase(MMSDKConstants.TEXT_MALE)) {
    		gender = MMSDKConstants.NUM_MALE;
    	} else if(etGender.getText().toString().equalsIgnoreCase(MMSDKConstants.TEXT_FEMALE)) {
    		gender = MMSDKConstants.NUM_FEMALE;
    	}
    	return gender;
    }
    
    /**
     * Display an {@link AlertDialog} with the associated message informing user that they forgot enter a certain input field.
     * @param messageId String resource id of the message to be displayed
     */
    private void displayAlert(int messageId) {
    	new AlertDialog.Builder(SignUpTwitterScreen.this)
    		.setTitle(R.string.app_name)
    		.setMessage(messageId)
    		.setNeutralButton(android.R.string.ok, null)
    		.show();
    }
    
    /**
     * Custom {@link MMCallback} specifically for {@link SignUpTwitterScreen} to be processed after receiving response from MobMonkey server.
     * @author Dezapp, LLC
     *
     */
    private class SignUpTwitterCallback implements MMCallback {
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(SignUpTwitterScreen.this, getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject response = new JSONObject((String) obj);
						if(response.getString(MMSDKConstants.JSON_KEY_ID).equals(MMSDKConstants.RESPONSE_ID_SUCCESS)) {
							Toast.makeText(SignUpTwitterScreen.this, R.string.toast_sign_up_successful, Toast.LENGTH_SHORT).show();
							setResult(Activity.RESULT_OK);
							userPrefsEditor.putString(MMSDKConstants.KEY_USER, providerUserName);
							userPrefsEditor.putString(MMSDKConstants.KEY_AUTH, getIntent().getStringExtra(MMSDKConstants.KEY_OAUTH_TOKEN));
							userPrefsEditor.commit();
							finish();
							overridePendingTransition(R.anim.slide_hold, R.anim.slide_right_out);
						} else {
							Toast.makeText(SignUpTwitterScreen.this, response.getString(MMSDKConstants.JSON_KEY_DESCRIPTION), Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			Log.d(TAG, TAG + "response: " + (String)obj);
		}
    }
}