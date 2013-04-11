package com.mobmonkey.mobmonkey.fragments;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkey.utils.MMProgressDialog;
import com.mobmonkey.mobmonkey.utils.MMUtility;
import com.mobmonkey.mobmonkeyapi.adapters.MMUserAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;

/**
 * Android {@link Fragment} screen displays the user's information.
 * @author Dezapp, LLC
 *
 */
public class MyInfoFragment extends MMFragment implements OnKeyListener, OnDateChangedListener, OnTouchListener {
	private static final String TAG = "MyInfoFragment: ";

	private SharedPreferences userPrefs;
	
	private InputMethodManager inputMethodManager;
	private EditText etFirstName, etLastName, etEmailAddress, etNewPassword, etConfirmPassword, etBirthdate, etGender;
	private MotionEvent prevEvent;
	
	private Calendar birthdate;
	
	private JSONObject response;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Activity.MODE_PRIVATE);
        
		MMProgressDialog.displayDialog(getActivity(), MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_loading_user_info));
		
		MMUserAdapter.getUserInfo(new UserInfoCallback(), MMConstants.PARTNER_ID,
				 userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
				 userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
		
		View view = inflater.inflate(R.layout.fragment_myinfo_screen, container, false);
    	
    	inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    	
    	etFirstName = (EditText) view.findViewById(R.id.etfirstname);
    	etLastName = (EditText) view.findViewById(R.id.etlastname);
    	etEmailAddress = (EditText) view.findViewById(R.id.etemailaddress);
    	etEmailAddress.setFocusable(false);
		etEmailAddress.setFocusableInTouchMode(false);
		etEmailAddress.setClickable(false);
		etNewPassword = (EditText) view.findViewById(R.id.etnewpassword);
		etConfirmPassword = (EditText) view.findViewById(R.id.etconfirmpassword);
    	etBirthdate = (EditText) view.findViewById(R.id.etbirthdate);
    	etGender = (EditText) view.findViewById(R.id.etgender);
    	birthdate = Calendar.getInstance();
    	
    	Log.d(TAG, "User has login with " + userPrefs.getString(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.DEFAULT_STRING) + " account.");
    	
    	// if user signed in with facebook account, they can edit nothing in this screen.
    	if(userPrefs.getString(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.DEFAULT_STRING).equals(MMAPIConstants.OAUTH_PROVIDER_FACEBOOK))
    	{
    		etFirstName.setFocusable(false);
    		etFirstName.setClickable(false);
    		
    		etLastName.setFocusable(false);
    		etLastName.setClickable(false);
    		
    		etBirthdate.setFocusable(false);
    		etBirthdate.setClickable(false);
    		
    		etBirthdate.setFocusable(false);
    		etBirthdate.setClickable(false);
    		
    		// make new password and confirm password disappear
    		etNewPassword.setVisibility(View.GONE);
    		etConfirmPassword.setVisibility(View.GONE);
    	}
    	// if user signed in with twitter account, they can edit every fields except email and password
    	else if(userPrefs.getString(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.DEFAULT_STRING).equals(MMAPIConstants.OAUTH_PROVIDER_TWITTER)) {
    		
    		// disable email
    		etEmailAddress.setFocusable(false);
    		etEmailAddress.setClickable(false);
    		
    		// disable password
    		etNewPassword.setFocusable(false);
    		etNewPassword.setClickable(false);
    		etConfirmPassword.setFocusable(false);
    		etConfirmPassword.setClickable(false);
    		
    		// set Listener to birth date, and Gender fields
    		etBirthdate.setOnTouchListener(MyInfoFragment.this);
    		etGender.setOnTouchListener(MyInfoFragment.this);
    		
    		// make new password and confirm password disappear
    		etNewPassword.setVisibility(View.GONE);
    		etConfirmPassword.setVisibility(View.GONE);
    	} 
    	// if user signed in with mobmonkey account, they can edit all fields but the email.
    	else {
    		//etEmailAddress.setOnKeyListener(MyInfoFragment.this);
    		// disable email
    		etEmailAddress.setFocusable(false);
    		etEmailAddress.setClickable(false);
    		
        	etBirthdate.setOnTouchListener(MyInfoFragment.this);
        	etGender.setOnTouchListener(MyInfoFragment.this);
    	}
    	
		return view;
	}

	@Override
	public void onFragmentBackPressed() {
		//TODO: add call to server to update the user information
		Log.d(TAG, response.toString());
		// check if newPassword is the same as confirm password
		String newPassword = etNewPassword.getText().toString(), 
			   confirmPassword = etConfirmPassword.getText().toString();
		if(newPassword.equals(confirmPassword)) {
			try {
				MMUserAdapter.updateUserInfo(new UserInfoUpdateCallback(), 
													   userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
													   userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), 
													   MMConstants.PARTNER_ID, 
													   newPassword, 
													   etFirstName.getText().toString(), 
													   etLastName.getText().toString(), 
													   response.getLong(MMAPIConstants.KEY_BIRTHDATE), 
													   convertGender(), 
													   response.getString(MMAPIConstants.KEY_CITY), 
													   response.getString(MMAPIConstants.KEY_STATE), 
													   response.getString(MMAPIConstants.KEY_ZIP), 
													   response.getBoolean(MMAPIConstants.KEY_ACCEPTEDTOS));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
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

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		
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
			inputMethodManager.hideSoftInputFromWindow(etEmailAddress.getWindowToken(), 0);
			return true;
		}
		return false;
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
    	LayoutInflater layoutInflator = LayoutInflater.from(getActivity());
    	View vBirthdate = layoutInflator.inflate(R.layout.birthdate_picker, null);
    	
    	final DatePicker dpBirthdate = (DatePicker) vBirthdate.findViewById(R.id.dpbirthdate);
    	
    	if(birthdate == null) {
	    	birthdate = Calendar.getInstance();
	    	birthdate.setTimeInMillis(System.currentTimeMillis());
	    	birthdate.set(birthdate.get(Calendar.YEAR) - 21, birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH));
		    dpBirthdate.init(birthdate.get(Calendar.YEAR), birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH), MyInfoFragment.this);
    	} else {
    		dpBirthdate.init(birthdate.get(Calendar.YEAR), birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH), MyInfoFragment.this);
    	}
    	
    	new AlertDialog.Builder(getActivity())
    		.setTitle(R.string.ad_title_birthdate)
    		.setView(vBirthdate)
    		.setCancelable(false)
    		.setPositiveButton(R.string.ad_btn_choose, new DialogInterface.OnClickListener() {
    			@Override
				public void onClick(DialogInterface dialog, int which) {
					birthdate.set(dpBirthdate.getYear(), dpBirthdate.getMonth(), dpBirthdate.getDayOfMonth());
					etBirthdate.setText(MMUtility.getDate(birthdate.getTimeInMillis(), "MM-dd-yyyy"));
				}
			})
			.setNegativeButton(R.string.ad_btn_cancel, null)
			.show();
    }
    
    /**
     * Prompt the user with an {@link AlertDialog} for his/her gender.
     */
    private void promptUserGender() {
    	new AlertDialog.Builder(getActivity())
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
    
    public void setUserInfo() {
    	try{
        	etFirstName.setText(response.getString(MMAPIConstants.KEY_FIRST_NAME));
        	etLastName.setText(response.getString(MMAPIConstants.KEY_LAST_NAME));
        	etEmailAddress.setText(response.getString(MMAPIConstants.KEY_EMAIL_ADDRESS));
        	if(response.getInt(MMAPIConstants.KEY_GENDER) == MMAPIConstants.NUM_MALE)
        		etGender.setText(MMAPIConstants.TEXT_MALE);
        	else if(response.getInt(MMAPIConstants.KEY_GENDER) == MMAPIConstants.NUM_FEMALE)
        		etGender.setText(MMAPIConstants.TEXT_FEMALE);
        	birthdate.setTimeInMillis(response.getLong(MMAPIConstants.KEY_BIRTHDATE));
        	etBirthdate.setText(MMUtility.getDate(birthdate.getTimeInMillis(), "MMM dd, yyyy"));
    	} catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    private class UserInfoCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				try {
					response = new JSONObject((String)obj);
					setUserInfo();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
    
    private class UserInfoUpdateCallback implements MMCallback {

		@Override
		public void processCallback(Object obj) {
			Log.d(TAG, (String) obj);
		}
    }
}
