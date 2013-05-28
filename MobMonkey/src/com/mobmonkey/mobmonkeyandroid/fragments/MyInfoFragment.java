package com.mobmonkey.mobmonkeyandroid.fragments;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * Android {@link Fragment} screen displays the user's information.
 * @author Dezapp, LLC
 *
 */
public class MyInfoFragment extends MMFragment implements OnClickListener,
														  OnKeyListener,
														  OnTouchListener,
														  OnDateChangedListener {
	private static final String TAG = "MyInfoFragment: ";

	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	private GraphUser facebookUser;
	
	private InputMethodManager inputMethodManager;
	
	private Button btnSave;
	private EditText etFirstName;
	private EditText etLastName;
	private EditText etEmailAddress;
	private EditText etNewPassword;
	private EditText etConfirmPassword;
	private EditText etBirthdate;
	private EditText etGender;
	
	private Calendar birthdate;
	
	private JSONObject response;
	
	private String oAuthProvider;
	private String newPassword;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Activity.MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
    	oAuthProvider = userPrefs.getString(MMSDKConstants.KEY_OAUTH_PROVIDER, MMSDKConstants.DEFAULT_STRING_EMPTY);
    	if(oAuthProvider.equals(MMSDKConstants.OAUTH_PROVIDER_FACEBOOK)) {
    		Session session = Session.getActiveSession();
			Session.NewPermissionsRequest request = new Session.NewPermissionsRequest(getActivity(), Arrays.asList(MMSDKConstants.FACEBOOK_REQ_PERM_EMAIL, MMSDKConstants.FACEBOOK_REQ_PERM_BIRTHDAY));
			session.requestNewReadPermissions(request);
			Request.executeMeRequestAsync(session, new RequestGraphUserCallback());
    	} else {
			MMUserAdapter.getUserInfo(new UserInfoCallback(),
									  MMConstants.PARTNER_ID,
									  userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
									  userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
			MMProgressDialog.displayDialog(getActivity(),
										   MMSDKConstants.DEFAULT_STRING_EMPTY,
										   getString(R.string.pd_loading_user_info));
    	}
		
    	inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		View view = inflater.inflate(R.layout.fragment_myinfo_screen, container, false);
		btnSave = (Button) view.findViewById(R.id.btnsave);
    	ScrollView svMyInfo = (ScrollView) view.findViewById(R.id.svmyinfo);
    	etFirstName = (EditText) view.findViewById(R.id.etfirstname);
    	etLastName = (EditText) view.findViewById(R.id.etlastname);
    	etEmailAddress = (EditText) view.findViewById(R.id.etemailaddress);
		etNewPassword = (EditText) view.findViewById(R.id.etnewpassword);
		etConfirmPassword = (EditText) view.findViewById(R.id.etconfirmpassword);
    	etBirthdate = (EditText) view.findViewById(R.id.etbirthdate);
    	etGender = (EditText) view.findViewById(R.id.etgender);
    	birthdate = Calendar.getInstance();
    	
		btnSave.setOnClickListener(MyInfoFragment.this);
    	svMyInfo.setOnTouchListener(MyInfoFragment.this);
    	etEmailAddress.setFocusable(false);
		etEmailAddress.setFocusableInTouchMode(false);
		etEmailAddress.setClickable(false);
    	
    	Log.d(TAG, "User has login with " + userPrefs.getString(MMSDKConstants.KEY_OAUTH_PROVIDER, MMSDKConstants.DEFAULT_STRING_EMPTY) + " account.");
    	
    	// if user signed in with Facebook account, they can edit nothing in this screen.
    	if(oAuthProvider.equals(MMSDKConstants.OAUTH_PROVIDER_FACEBOOK)) {
    		btnSave.setVisibility(View.INVISIBLE);
    		
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

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MMOnFragmentFinishListener) {
			fragmentFinishListener = (MMOnFragmentFinishListener) activity;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, TAG + "onActivityResult");
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnsave:
				saveUserInfo();
				break;
		}
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
     * {@link OnTouchListener} handler for birthdate and gender {@link EditText}. When the {@link EditText}s are touched, it will prompt the user to select his/her birthdate or gender.
     */
    /*
     * (non-Javadoc)
     * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
     */
    @Override
	public boolean onTouch(View v, MotionEvent event) {		
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

    /*
     * (non-Javadoc)
     * @see android.widget.DatePicker.OnDateChangedListener#onDateChanged(android.widget.DatePicker, int, int, int)
     */
	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		
	}
	
	@Override
	public void onFragmentBackPressed() {
		
		fragmentFinishListener.onFragmentFinish();
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
    	}    	
	    dpBirthdate.init(birthdate.get(Calendar.YEAR), birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DAY_OF_MONTH), MyInfoFragment.this);
	    
    	new AlertDialog.Builder(getActivity())
    		.setTitle(R.string.ad_title_birthdate)
    		.setView(vBirthdate)
    		.setCancelable(false)
    		.setPositiveButton(R.string.ad_btn_choose, new DialogInterface.OnClickListener() {
    			@Override
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
    
    /**
     * 
     */
    public void setUserInfo() {
    	try{
        	etFirstName.setText(response.getString(MMSDKConstants.KEY_FIRST_NAME));
        	etLastName.setText(response.getString(MMSDKConstants.KEY_LAST_NAME));
        	etEmailAddress.setText(response.getString(MMSDKConstants.KEY_EMAIL_ADDRESS));
        	birthdate.setTimeInMillis(response.getLong(MMSDKConstants.KEY_BIRTHDATE));
        	etBirthdate.setText(MMUtility.getDate(birthdate.getTimeInMillis(), MMSDKConstants.DATE_FORMAT_MMM_DD_COMMA_YYYY));
        	if(response.getInt(MMSDKConstants.KEY_GENDER) == MMSDKConstants.NUM_MALE)
        		etGender.setText(MMSDKConstants.TEXT_MALE);
        	else if(response.getInt(MMSDKConstants.KEY_GENDER) == MMSDKConstants.NUM_FEMALE)
        		etGender.setText(MMSDKConstants.TEXT_FEMALE);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
	/**
     * Function that check if the first name {@link EditText} field is valid and is not empty and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkFields() {
    	if(TextUtils.isEmpty(etFirstName.getText().toString())) {
    		displayAlert(R.string.ad_message_first_name_empty);
    		return false;
    	}
    	else {
    		return checkLastName();
    	}
    }
    
    /**
     * Function that check if the last name {@link EditText} field is valid and is not empty and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkLastName() {
    	if(TextUtils.isEmpty(etLastName.getText().toString())) {
    		displayAlert(R.string.ad_message_last_name_empty);
    		return false;
    	}
    	else {
    		return checkPassword();
    	}
    }
    
    /**
     * Function that check if the password {@link EditText} fields is empty, set the password to the oldPassword. Else check if the password fields are the same. 
     * In addition, it compare the passwords to determine if they are equal and and stored the value into a {@link HashMap}.
     * @return <code>false</code> otherwise
     */
    private boolean checkPassword() {
    	if(TextUtils.isEmpty(newPassword)) {
    		if(TextUtils.isEmpty(etConfirmPassword.getText())) {
        		newPassword = userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY);
        		return checkBirthdate();
    		} else {
    			displayAlert(R.string.ad_message_password_not_match);
    			return false;
    		}
    	} else if(!newPassword.equals(etConfirmPassword.getText())){
    		displayAlert(R.string.ad_message_password_not_match);
			userPrefsEditor.putString(MMSDKConstants.KEY_PASSWORD, newPassword);
			userPrefsEditor.commit();
    		return false;
    	}
    	return true;
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
    		return true;
    	} else {
    		displayAlert(R.string.ad_message_invalid_gender);
    		return false;
    	}
    }
    
    /**
     * 
     */
    private void saveUserInfo() {
		newPassword = etNewPassword.getText().toString(); 
		if(checkFields()) {
			try {
				MMProgressDialog.displayDialog(getActivity(),
											   MMSDKConstants.DEFAULT_STRING_EMPTY,
											   getString(R.string.pd_updating_user_info));
				MMUserAdapter.updateUserInfo(new UserInfoUpdateCallback(),
										   	 etFirstName.getText().toString(),
										   	 etLastName.getText().toString(),
										   	 newPassword,
										   	 birthdate.getTimeInMillis(),
										   	 convertGender(),
										   	 response.getString(MMSDKConstants.KEY_CITY),
										   	 response.getString(MMSDKConstants.KEY_STATE),
										   	 response.getString(MMSDKConstants.KEY_ZIP),
										   	 response.getBoolean(MMSDKConstants.KEY_ACCEPTEDTOS),
											 MMConstants.PARTNER_ID,
											 userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
										   	 userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    }
    
    /**
     * 
     * @param messageId
     */
    private void displayAlert(int messageId) {
		new AlertDialog.Builder(getActivity())
			.setTitle(R.string.app_name)
			.setMessage(messageId)
			.setNeutralButton(android.R.string.ok, null)
			.show();
	}
    
    /**
     * 
     * @author Dezapp, LLC
     *
     */
    private class UserInfoCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						response = new JSONObject((String) obj);
						setUserInfo();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
    
    /**
     * 
     * @author Dezapp, LLC
     *
     */
    private class UserInfoUpdateCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObj = new JSONObject((String) obj);
						if(jObj.getString(MMSDKConstants.JSON_KEY_STATUS).equals(MMSDKConstants.RESPONSE_STATUS_SUCCESS)) {
							Toast.makeText(getActivity(), jObj.getString(MMSDKConstants.JSON_KEY_DESCRIPTION), Toast.LENGTH_SHORT).show();
							fragmentFinishListener.onFragmentFinish();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
    }
	
	/**
	 * Custom {@link Request.GraphUserCallback} specifically for {@link MyInfoFragment} to the completion of the {@link Request}.executeMeRequestAsync({@link Session}, {@link Request.GraphUserCallback}).
	 * @author Dezapp, LLC
	 *
	 */
	private class RequestGraphUserCallback implements Request.GraphUserCallback {
		@Override
		public void onCompleted(GraphUser user, Response response) {
			Log.d(TAG, TAG + "onCompleted");
			if(user != null) {
				facebookUser = user;			
				etFirstName.setText(facebookUser.getFirstName());
	        	etLastName.setText(facebookUser.getLastName());
	        	etEmailAddress.setText((String) facebookUser.getProperty(MMSDKConstants.FACEBOOK_REQ_PERM_EMAIL));
	        	etBirthdate.setText(facebookUser.getBirthday());
	        	etGender.setText((String) facebookUser.getProperty(MMSDKConstants.FACEBOOK_REQ_PERM_GENDER));
			}
		}
	}    
}
