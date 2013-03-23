package com.mobmonkey.mobmonkey;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.mobmonkey.mobmonkey.utils.MMMyInfoArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMMyinfoItem;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ListView;

public class MyInfoScreen extends Activity implements OnKeyListener, OnDateChangedListener, OnTouchListener{
	private static final String TAG = "MyInfoScreen: ";

	SharedPreferences userPrefs;
	SharedPreferences.Editor userPrefsEditor;
	
	InputMethodManager inputMethodManager;
	EditText etFirstName;
	EditText etLastName;
	EditText etEmailAddress;
	EditText etPassword;
	EditText etPasswordConfirm;
	EditText etBirthdate;
	EditText etGender;
	MotionEvent prevEvent;

	Calendar birthdate;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_info_screen);
		
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
		
		if(event.getAction() == MotionEvent.ACTION_UP) {
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
    	
    	etPasswordConfirm.setOnKeyListener(MyInfoScreen.this);
    	etBirthdate.setOnTouchListener(MyInfoScreen.this);
    	etGender.setOnTouchListener(MyInfoScreen.this);
	}
	
	/**
	 * Function that overrides Android back button to save user information for all fields
	 * @return 
	 * @return
	 */
	@Override
	public void onBackPressed()
	{
		userPrefsEditor.putString(MMAPIConstants.KEY_FIRST_NAME, etFirstName.getText().toString());
		userPrefsEditor.putString(MMAPIConstants.KEY_LAST_NAME, etLastName.getText().toString());
		userPrefsEditor.putString(MMAPIConstants.KEY_EMAIL_ADDRESS, etEmailAddress.getText().toString());
		userPrefsEditor.putString(MMAPIConstants.KEY_PASSWORD, etPassword.getText().toString());
		userPrefsEditor.putLong(MMAPIConstants.KEY_BIRTHDATE, birthdate.getTimeInMillis());
		userPrefsEditor.putInt(MMAPIConstants.KEY_GENDER, convertGender());
		userPrefsEditor.commit();
		
		// TODO For testing, remove later
		Log.d( TAG, userPrefs.getString(MMAPIConstants.KEY_FIRST_NAME, "fail") + ", " +
				userPrefs.getString(MMAPIConstants.KEY_LAST_NAME, "fail") + ", " +
				userPrefs.getString(MMAPIConstants.KEY_EMAIL_ADDRESS, "fail") + ", " +
				userPrefs.getString(MMAPIConstants.KEY_PASSWORD, "fail") + ", " +
				userPrefs.getLong(MMAPIConstants.KEY_BIRTHDATE, -1) + ", " +
				userPrefs.getInt(MMAPIConstants.KEY_GENDER, -1) );
		
		finish();
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
    	} else {
    		gender = (Integer) null;
    	}
    	return gender;
    }
    
    /**
     * Prompt the user with an {@link AlertDialog} to select his/her birthdate.
     */
    private void promptUserBirthdate() {
    	LayoutInflater layoutInflator = LayoutInflater.from(MyInfoScreen.this);
    	View vBirthdate = layoutInflator.inflate(R.layout.birthdate_picker, null);
    	
    	final DatePicker dpBirthdate = (DatePicker) vBirthdate.findViewById(R.id.dpbirthdate);
    	
    	if(birthdate == null) {
	    	birthdate = Calendar.getInstance();
	    	birthdate.setTimeInMillis(System.currentTimeMillis());
	    	birthdate.set(birthdate.get(Calendar.YEAR) - 21, birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH));
		    dpBirthdate.init(birthdate.get(Calendar.YEAR), birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH), MyInfoScreen.this);
    	} else {
    		dpBirthdate.init(birthdate.get(Calendar.YEAR), birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH), MyInfoScreen.this);
    	}
    	
    	new AlertDialog.Builder(MyInfoScreen.this)
    		.setTitle(R.string.ad_title_birthdate)
    		.setView(vBirthdate)
    		.setCancelable(false)
    		.setPositiveButton(R.string.ad_btn_choose, new DialogInterface.OnClickListener() {
    			@Override
				public void onClick(DialogInterface dialog, int which) {
					birthdate.set(dpBirthdate.getYear(), dpBirthdate.getMonth(), dpBirthdate.getDayOfMonth());
					Date tempDate = new Date(birthdate.get(Calendar.YEAR) - 1900, birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH));
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
					etBirthdate.setText(simpleDateFormat.format(tempDate));
				}
			})
			.setNegativeButton(R.string.ad_btn_cancel, null)
			.show();
    }
    
    /**
     * Prompt the user with an {@link AlertDialog} for his/her gender.
     */
    private void promptUserGender() {
    	new AlertDialog.Builder(MyInfoScreen.this)
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
	 * Handle events when the date changes on the {@link DatePicker}
	 */
	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		
	}
	
}
