package com.mobmonkey.mobmonkey.fragments;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkey.MakeARequestScreen;
import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.SearchScreen;
import com.mobmonkey.mobmonkey.R.array;
import com.mobmonkey.mobmonkey.R.id;
import com.mobmonkey.mobmonkey.R.layout;
import com.mobmonkey.mobmonkey.R.string;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkey.utils.MMUtility;
import com.mobmonkey.mobmonkeyapi.adapters.MMSignUpAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;

/**
 * @author Dezapp, LLC
 *
 */
public class MyInfoFragment extends MMFragment implements OnKeyListener, OnDateChangedListener, OnTouchListener {
	private static final String TAG = "MyInfoFragment: ";

	SharedPreferences userPrefs;
	
	InputMethodManager inputMethodManager;
	EditText etFirstName;
	EditText etLastName;
	EditText etEmailAddress;
	EditText etBirthdate;
	EditText etGender;
	MotionEvent prevEvent;
	
	Calendar birthdate;
	
	ProgressDialog progressDialog;
	
	JSONObject response;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Activity.MODE_PRIVATE);
        
		progressDialog = ProgressDialog.show(getActivity(), MMAPIConstants.DEFAULT_STRING, "Loading user info...", true, false);
		
		MMSignUpAdapter.getUserInfo(new UserInfoCallback(), MMConstants.PARTNER_ID,
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
    	etBirthdate = (EditText) view.findViewById(R.id.etbirthdate);
    	etGender = (EditText) view.findViewById(R.id.etgender);
    	birthdate = Calendar.getInstance();
    	
    	etEmailAddress.setOnKeyListener(MyInfoFragment.this);
    	etBirthdate.setOnTouchListener(MyInfoFragment.this);
    	etGender.setOnTouchListener(MyInfoFragment.this);
    	
		return view;
	}

	@Override
	public void onFragmentBackPressed() {
		//TODO: add call to server to update the user information
		
//		userPrefsEditor.putString(MMAPIConstants.KEY_FIRST_NAME, etFirstName.getText().toString());
//		userPrefsEditor.putString(MMAPIConstants.KEY_LAST_NAME, etLastName.getText().toString());
//		userPrefsEditor.putString(MMAPIConstants.KEY_EMAIL_ADDRESS, etEmailAddress.getText().toString());
//		if(!TextUtils.isEmpty(etBirthdate.getText())) {
//			userPrefsEditor.putLong(MMAPIConstants.KEY_BIRTHDATE, birthdate.getTimeInMillis());
//		}
//		if(!TextUtils.isEmpty(etGender.getText())) {
//			userPrefsEditor.putInt(MMAPIConstants.KEY_GENDER, convertGender());
//		}
//		userPrefsEditor.commit();
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
        	etBirthdate.setText(MMUtility.getDate(birthdate.getTimeInMillis(), "MM-dd-yyyy"));
    	} catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    private class UserInfoCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				try {
					if(progressDialog != null)
						progressDialog.dismiss();
					response = new JSONObject((String)obj);
					setUserInfo();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
