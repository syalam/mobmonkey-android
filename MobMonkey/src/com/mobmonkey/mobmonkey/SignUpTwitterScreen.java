package com.mobmonkey.mobmonkey;

import java.io.ObjectOutputStream.PutField;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkeyapi.adapters.MMSignUpAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;

/**
 * @author Dezapp, LLC
 *
 */
public class SignUpTwitterScreen extends Activity implements OnTouchListener, OnDateChangedListener {
	private static final String TAG = "SignUpTwitter: ";
	
	SharedPreferences userPrefs;
	SharedPreferences.Editor userPrefsEditor;
	
	String providerUserName;
	
	MotionEvent prevEvent;
	ProgressDialog progressDialog;
	InputMethodManager inputMethodManager;
	
	HashMap<String,Object> userInfo;
	
	TextView tvProviderUserName;
	EditText etFirstName;
	EditText etLastName;
	EditText etEmailAddress;
	EditText etBirthdate;
	EditText etGender;
	
	Calendar birthdate;
	String userEmail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signuptwitter);
		
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
	}

	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			prevEvent = event;
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP) {
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

	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		
	}
	
	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btnsignup:
				signUpTwitter();
				break;
		}
	}
	
	private void init() {
    	userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
    	userPrefsEditor = userPrefs.edit();
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		providerUserName = getIntent().getStringExtra(MMAPIConstants.KEY_OAUTH_PROVIDER_USER_NAME);
		
    	userInfo = new HashMap<String,Object>();
    	userInfo.put(MMAPIConstants.KEY_OAUTH_PROVIDER_USER_NAME, providerUserName);
    	userInfo.put(MMAPIConstants.KEY_OAUTH_TOKEN, getIntent().getStringExtra(MMAPIConstants.KEY_OAUTH_TOKEN));
    	
    	tvProviderUserName = (TextView) findViewById(R.id.tvproviderusername);
    	etFirstName = (EditText) findViewById(R.id.etfirstname);
    	etLastName = (EditText) findViewById(R.id.etlastname);
    	etEmailAddress = (EditText) findViewById(R.id.etemailaddress);
    	etBirthdate = (EditText) findViewById(R.id.etbirthdate);
    	etGender = (EditText) findViewById(R.id.etgender);
    	
    	tvProviderUserName.setText("@" + providerUserName + " user info");
    	etBirthdate.setOnTouchListener(SignUpTwitterScreen.this);
    	etGender.setOnTouchListener(SignUpTwitterScreen.this);
    	
    	// TODO: Hardcoded values, to be removed
    	etFirstName.setText("Scumbagu");
    	etLastName.setText("Hanku");
    	etEmailAddress.setText("scumbaghank@gmail.com");
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
    	new AlertDialog.Builder(SignUpTwitterScreen.this)
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
     * 
     */
    private void signUpTwitter() {
    	if(checkFirstName()) {
    		MMSignUpAdapter.signUpNewUserTwitter(new SignUpTwitterCallback(), userInfo, MMConstants.PARTNER_ID);
    		progressDialog = ProgressDialog.show(SignUpTwitterScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_signing_up), true, false);
    	}
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
//    		userPrefsEditor.putString(MMAPIConstants.KEY_EMAIL_ADDRESS, etEmailAddress.getText().toString());
//    		userPrefsEditor.commit();
    		return checkBirthdate();
    	} else {
    		displayAlert(R.string.alert_invalid_email_address);
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
    		return true;
    	} else {
    		displayAlert(R.string.alert_invalid_gender);
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
    	new AlertDialog.Builder(SignUpTwitterScreen.this)
    		.setTitle(R.string.app_name)
    		.setMessage(messageId)
    		.setNeutralButton(android.R.string.ok, null)
    		.show();
    }
    
    private class SignUpTwitterCallback implements MMCallback {
		public void processCallback(Object obj) {
			if(progressDialog != null) {
				progressDialog.dismiss();
			}
			
			try {
				JSONObject response = new JSONObject((String) obj);
				if(response.getString(MMAPIConstants.KEY_RESPONSE_ID).equals(MMAPIConstants.RESPONSE_ID_SUCCESS)) {
					Toast.makeText(SignUpTwitterScreen.this, R.string.toast_sign_up_successful, Toast.LENGTH_SHORT).show();
					setResult(Activity.RESULT_OK);
					userPrefsEditor.putString(MMAPIConstants.KEY_USER, providerUserName);
					userPrefsEditor.putString(MMAPIConstants.KEY_AUTH, getIntent().getStringExtra(MMAPIConstants.KEY_OAUTH_TOKEN));
					userPrefsEditor.commit();
					finish();
				} else {
					Toast.makeText(SignUpTwitterScreen.this, response.getString(MMAPIConstants.KEY_RESPONSE_DESC), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d(TAG, TAG + "response: " + (String)obj);
		}
    }
}