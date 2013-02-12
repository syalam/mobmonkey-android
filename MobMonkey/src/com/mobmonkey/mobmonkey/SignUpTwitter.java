package com.mobmonkey.mobmonkey;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;

/**
 * @author Dezapp, LLC
 *
 */
public class SignUpTwitter extends Activity implements OnTouchListener, OnDateChangedListener {
	SharedPreferences userPrefs;
	
	MotionEvent prevEvent;
	InputMethodManager inputMethodManager;
	
	HashMap<String,Object> hashMap;
	
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
	
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnsignup:
				signUpTwitter();
				break;
		}
	}
	
	private void init() {
    	userPrefs = getSharedPreferences("USER_PREFS", MODE_PRIVATE);
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
    	hashMap = new HashMap<String,Object>();
    	hashMap.put("OAuth", userPrefs.getString("", ""));
    	
    	etFirstName = (EditText) findViewById(R.id.etfirstname);
    	etLastName = (EditText) findViewById(R.id.etlastname);
    	etEmailAddress = (EditText) findViewById(R.id.etemailaddress);
    	etBirthdate = (EditText) findViewById(R.id.etbirthdate);
    	etGender = (EditText) findViewById(R.id.etgender);
    	
    	etBirthdate.setOnTouchListener(SignUpTwitter.this);
    	etGender.setOnTouchListener(SignUpTwitter.this);
    	
    	// TODO: Hardcoded values, to be removed
    	etFirstName.setText("Wilson");
    	etLastName.setText("Xie");
    	etEmailAddress.setText("Wilson@dezapp.com");
	}
	
    /**
     * Prompt the user with an {@link AlertDialog} to select his/her birthdate.
     */
    private void promptUserBirthdate() {
    	LayoutInflater layoutInflator = LayoutInflater.from(SignUpTwitter.this);
    	View vBirthdate = layoutInflator.inflate(R.layout.birthdate_picker, null);
    	
    	final DatePicker dpBirthdate = (DatePicker) vBirthdate.findViewById(R.id.dpbirthdate);
    	
    	if(birthdate == null) {
	    	birthdate = Calendar.getInstance();
	    	birthdate.setTimeInMillis(System.currentTimeMillis());
	    	birthdate.set(birthdate.get(Calendar.YEAR) - 21, birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH));
		    dpBirthdate.init(birthdate.get(Calendar.YEAR), birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH), SignUpTwitter.this);
    	} else {
    		dpBirthdate.init(birthdate.get(Calendar.YEAR), birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH), SignUpTwitter.this);
    	}
    	
    	new AlertDialog.Builder(SignUpTwitter.this)
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
    	new AlertDialog.Builder(SignUpTwitter.this)
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
    
    private void signUpTwitter() {
    	
    }
}