package com.mobmonkey.mobmonkey;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkeyapi.servercalls.MMSignUp;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMGetDeviceUUID;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;
import android.text.TextUtils;

public class SignUpScreen extends Activity implements OnDateChangedListener, OnTouchListener {
	private static final String TAG = "SignUpScreen: ";
	
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
	
	// TODO: remove soft keyboard from birthdate and gender
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: move this to the first screen of the app 
        MMGetDeviceUUID.setContext(getApplicationContext());
        
        setContentView(R.layout.signupscreen);
        initUserInfoFields();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void viewOnClick(View view) {
    	switch(view.getId()) {
	    	case R.id.btnsignup:
	    		signUpNormal();
	    		break;
	    	case R.id.btnsignupfacebook:
	    		// TODO: 
	    		break;
	    	case R.id.btnsignuptwitter:
	    		// TODO: 
	    		break;
    	}
    }
    
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
    
    private void signUpNormal() {
    	if(checkFirstName()) {
    		MMSignUp.signUpNewUser(new SignUpCallback(), userInfo, MMConstants.PARTNER_ID);
    		progressDialog = ProgressDialog.show(SignUpScreen.this, MMAPIConstants.DEFAULT_STRING, "Signing up...", true, true);
    	}
    }
    
    private boolean checkFirstName() {
    	if(!TextUtils.isEmpty(etFirstName.getText())) {
    		userInfo.put(MMAPIConstants.KEY_FIRST_NAME, etFirstName.getText().toString());
    		return checkLastName();
    	} else {
    		displayAlert(R.string.alert_invalid_first_name);
    		return false;
    	}
    }
    
    private boolean checkLastName() {
    	if(!TextUtils.isEmpty(etLastName.getText().toString())) {
    		userInfo.put(MMAPIConstants.KEY_LAST_NAME, etLastName.getText().toString());
    		return checkEmailAddress();
    	} else {
    		displayAlert(R.string.alert_invalid_last_name);
    		return false;
    	}
    }
    
    private boolean checkEmailAddress() {
    	if(!TextUtils.isEmpty(etEmailAddress.getText())) {
    		userInfo.put(MMAPIConstants.KEY_EMAIL_ADDRESS, etEmailAddress.getText().toString());
    		return checkPassword();
    	} else {
    		displayAlert(R.string.alert_invalid_email_address);
    		return false;
    	}
    }
    
    private boolean checkPassword() {
    	if(!TextUtils.isEmpty(etPassword.getText()) && !TextUtils.isEmpty(etPasswordConfirm.getText())) {
    		if(etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())) {
    			userInfo.put(MMAPIConstants.KEY_PASSWORD, etPassword.getText().toString());
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
    
    private boolean checkBirthdate() {
    	if(!TextUtils.isEmpty(etBirthdate.getText())) {
    		userInfo.put(MMAPIConstants.KEY_BIRTHDATE, birthdate.getTimeInMillis());
    		return checkGender();
    	} else {
    		displayAlert(R.string.alert_invalid_birthdate);
    		return false;
    	}
    }
    
    private boolean checkGender() {
    	if(!TextUtils.isEmpty(etGender.getText())) {
    		userInfo.put(MMAPIConstants.KEY_GENDER, convertGender());
    		return checkAcceptedToS();
    	} else {
    		displayAlert(R.string.alert_invalid_gender);
    		return false;
    	}
    }
    
    private boolean checkAcceptedToS() {
    	if(cbAcceptedToS.isChecked()) {
    		userInfo.put(MMAPIConstants.KEY_ACCEPTEDTOS, cbAcceptedToS.isChecked());
    		return true;
    	} else {
    		displayAlert(R.string.alert_invalid_tos);
    		return false;
    	}
    }
    
    private int convertGender() {
    	int gender = MMAPIConstants.DEFAULT_INT;
    	if(etGender.getText().toString().equalsIgnoreCase(MMAPIConstants.TEXT_MALE)) {
    		gender = MMAPIConstants.NUM_MALE;
    	} else if(etGender.getText().toString().equalsIgnoreCase(MMAPIConstants.TEXT_FEMALE)) {
    		gender = MMAPIConstants.NUM_FEMALE;
    	}
    	return gender;
    }
    
    private void displayAlert(int messageId) {
    	new AlertDialog.Builder(SignUpScreen.this)
    		.setTitle(R.string.app_name)
    		.setMessage(messageId)
    		.setNeutralButton(android.R.string.ok, null)
    		.show();
    }
    
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
					Log.d(TAG, TAG + "dpBirthdate: " + birthdate.toString());
					Log.d(TAG, TAG + "dpBirthdateMilli: " + birthdate.getTimeInMillis());
					Date tempDate = new Date(birthdate.get(Calendar.YEAR) - 1900, birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH));
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
					etBirthdate.setText(simpleDateFormat.format(tempDate));
				}
			})
			.setNegativeButton(R.string.btn_cancel, null)
			.show();
    }
    
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
	
    private class SignUpCallback implements MMCallback {
		public void processCallback(Object obj) {
			if(progressDialog != null) {
				progressDialog.dismiss();
			}
			
			try {
				JSONObject response = new JSONObject((String) obj);
				if(response.getString("status").equals("Success")) {
					// TODO: go to profile screen?
					Toast.makeText(SignUpScreen.this, "Sign Up Successful", Toast.LENGTH_LONG).show();
				} else {
					// TODO: alert user
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d(TAG, TAG + "response: " + (String) obj);
		}	
    }
    
	public boolean onTouch(View v, MotionEvent event) {
		Log.d(TAG, TAG + "onTouch: " + event.getAction());
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			prevEvent = event;
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP) {
			switch(v.getId()) {
		    	case R.id.etbirthdate:
		    		imm.hideSoftInputFromWindow(etGender.getWindowToken(), 0);
		    		promptUserBirthdate();
		    		return true;
		    	case R.id.etgender:
		    		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		    		imm.hideSoftInputFromWindow(etGender.getWindowToken(), 0);
		    		promptUserGender();
		    		return true;
			}
		}
		
		return false;
	}

	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		
	}
}
