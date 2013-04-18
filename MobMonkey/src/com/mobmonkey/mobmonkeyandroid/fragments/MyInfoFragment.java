package com.mobmonkey.mobmonkeyandroid.fragments;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
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
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import android.widget.ScrollView;

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
	
	private String oAuthProvider;
	private String newPassword, confirmPassword;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Activity.MODE_PRIVATE);
        
		MMProgressDialog.displayDialog(getActivity(), MMSDKConstants.DEFAULT_STRING_EMPTY, getString(R.string.pd_loading_user_info));
		
		MMUserAdapter.getUserInfo(new UserInfoCallback(), MMConstants.PARTNER_ID,
				 userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY), 
				 userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
		
		View view = inflater.inflate(R.layout.fragment_myinfo_screen, container, false);
    	
    	inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    	
    	ScrollView svMyInfo = (ScrollView) view.findViewById(R.id.svmyinfo);
    	svMyInfo.setOnTouchListener(MyInfoFragment.this);
    	
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
    	
    	Log.d(TAG, "User has login with " + userPrefs.getString(MMSDKConstants.KEY_OAUTH_PROVIDER, MMSDKConstants.DEFAULT_STRING_EMPTY) + " account.");
    	
    	oAuthProvider = userPrefs.getString(MMSDKConstants.KEY_OAUTH_PROVIDER, MMSDKConstants.DEFAULT_STRING_EMPTY);
    	
    	// if user signed in with Facebook account, they can edit nothing in this screen.
    	if(oAuthProvider.equals(MMSDKConstants.OAUTH_PROVIDER_FACEBOOK)) {
    		etFirstName.setFocusable(false);
    		etFirstName.setClickable(false);
    		
    		etLastName.setFocusable(false);
    		etLastName.setClickable(false);
    		
    		etEmailAddress.setFocusable(false);
    		etEmailAddress.setClickable(false);
    		
    		etBirthdate.setFocusable(false);
    		etBirthdate.setClickable(false);
    		
    		etBirthdate.setFocusable(false);
    		etBirthdate.setClickable(false);
    		
    		etGender.setFocusable(false);
    		etGender.setClickable(false);
    		
    		// make new password and confirm password disappear
    		etNewPassword.setVisibility(View.GONE);
    		etConfirmPassword.setVisibility(View.GONE);
    	}
    	// if user signed in with twitter account, they can edit every fields except email and password
    	else if(oAuthProvider.equals(MMSDKConstants.OAUTH_PROVIDER_TWITTER)) {
    		
    		// disable email
    		etEmailAddress.setFocusable(false);
    		etEmailAddress.setClickable(false);
    		
    		// disable password
    		etNewPassword.setFocusable(false);
    		etNewPassword.setClickable(false);
    		etConfirmPassword.setFocusable(false);
    		etConfirmPassword.setClickable(false);
    		
    		// set Listener to birth date, and Gender fields
    		etLastName.setOnKeyListener(MyInfoFragment.this);
    		etBirthdate.setOnTouchListener(MyInfoFragment.this);
    		etGender.setOnTouchListener(MyInfoFragment.this);
    		
    		// make new password and confirm password disappear
    		etNewPassword.setVisibility(View.GONE);
    		etConfirmPassword.setVisibility(View.GONE);
    	} 
    	// if user signed in with mobmonkey account, they can edit all fields but the email.
    	else {
    		etEmailAddress.setFocusable(false);
    		etEmailAddress.setClickable(false);
    		
    		etConfirmPassword.setOnKeyListener(MyInfoFragment.this);
        	etBirthdate.setOnTouchListener(MyInfoFragment.this);
        	etGender.setOnTouchListener(MyInfoFragment.this);
    	}
    	
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnFragmentFinishListener) {
			onFragmentFinishListener = (OnFragmentFinishListener) activity;
		}
	}

	@Override
	public void onFragmentBackPressed() {
		Log.d(TAG, response.toString());
		// check if newPassword is the same as confirm password
		newPassword = etNewPassword.getText().toString(); 
		confirmPassword = etConfirmPassword.getText().toString();
		if(checkFields()) {
			try {
				MMUserAdapter.updateUserInfo(new UserInfoUpdateCallback(), 
										   	 userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY), 
										   	 userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY), 
										   	 MMConstants.PARTNER_ID, 
										   	 newPassword, 
										   	 etFirstName.getText().toString(),
										   	 etLastName.getText().toString(),
										   	 response.getLong(MMSDKConstants.KEY_BIRTHDATE), 
										   	 convertGender(), 
										   	 response.getString(MMSDKConstants.KEY_CITY), 
										   	 response.getString(MMSDKConstants.KEY_STATE), 
										   	 response.getString(MMSDKConstants.KEY_ZIP), 
										   	 response.getBoolean(MMSDKConstants.KEY_ACCEPTEDTOS));
				MMProgressDialog.displayDialog(getActivity(), MMSDKConstants.DEFAULT_STRING_EMPTY, getString(R.string.pd_updating_user_info));
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
    	Log.d(TAG, TAG + "Hank got touched again");
    	Log.d(TAG, TAG + "view id: " + v.getId() + "  scrollview id: " + R.id.svmyinfo);
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
			if(oAuthProvider.equals(MMSDKConstants.OAUTH_PROVIDER_TWITTER)) {
				inputMethodManager.hideSoftInputFromWindow(etLastName.getWindowToken(), 0);
				return true;
			} else {
				inputMethodManager.hideSoftInputFromWindow(etConfirmPassword.getWindowToken(), 0);
				return true;
			}
		}
		return false;
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
					etBirthdate.setText(MMUtility.getDate(birthdate.getTimeInMillis(), "MMM dd, yyyy"));
					try {
						response.put(MMSDKConstants.KEY_BIRTHDATE, birthdate.getTimeInMillis());
					} catch (JSONException e) {
						e.printStackTrace();
					}
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
        	etFirstName.setText(response.getString(MMSDKConstants.KEY_FIRST_NAME));
        	etLastName.setText(response.getString(MMSDKConstants.KEY_LAST_NAME));
        	etEmailAddress.setText(response.getString(MMSDKConstants.KEY_EMAIL_ADDRESS));
        	if(response.getInt(MMSDKConstants.KEY_GENDER) == MMSDKConstants.NUM_MALE)
        		etGender.setText(MMSDKConstants.TEXT_MALE);
        	else if(response.getInt(MMSDKConstants.KEY_GENDER) == MMSDKConstants.NUM_FEMALE)
        		etGender.setText(MMSDKConstants.TEXT_FEMALE);
        	birthdate.setTimeInMillis(response.getLong(MMSDKConstants.KEY_BIRTHDATE));
        	etBirthdate.setText(MMUtility.getDate(birthdate.getTimeInMillis(), "MMM dd, yyyy"));
    	} catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    private boolean checkFields() {
    	if(TextUtils.isEmpty(etFirstName.getText().toString())) {
    		// alert: First name cannot be empty.
    		displayAlert(R.string.ad_message_first_name_empty);
    		return false;
    	}
    	else {
    		return checkLastName();
    	}
    }
    
    private boolean checkLastName() {
    	if(TextUtils.isEmpty(etLastName.getText().toString())) {
    		// alert: Last name cannot be empty.
    		displayAlert(R.string.ad_message_last_name_empty);
    		return false;
    	}
    	else {
    		return checkPassword();
    	}
    }
    
    private boolean checkPassword() {
    	if(TextUtils.isEmpty(newPassword)) {
    		if(TextUtils.isEmpty(confirmPassword)) {
    			// if both new password and confirn password are empty, set it to the old password.
        		newPassword = userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY);
        		return true;
        	// else, if confirm password is not empty, and new password is empty, alert user.
    		} else {
    			displayAlert(R.string.ad_message_password_not_match);
    			return false;
    		}
    	// if new password and confirm password are not the same, alert user.
    	} else if(!newPassword.equals(confirmPassword)){
    		// alert: new password and confirm password is not the same.
    		displayAlert(R.string.ad_message_password_not_match);
    		return false;
    	}
    	return true;
    }
    
    private void displayAlert(int messageId) {
		new AlertDialog.Builder(getActivity())
			.setTitle(R.string.app_name)
			.setMessage(messageId)
			.setNeutralButton(android.R.string.ok, null)
			.show();
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
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				try {
					JSONObject jObj = new JSONObject((String) obj);
					if(jObj.getString(MMSDKConstants.JSON_KEY_STATUS).equals(MMSDKConstants.RESPONSE_STATUS_SUCCESS)) {
						onFragmentFinishListener.onFragmentFinish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
    }
}
