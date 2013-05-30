package com.mobmonkey.mobmonkeyandroid;

import java.util.Stack;

import org.json.JSONArray;

import com.facebook.Session;
import com.mobmonkey.mobmonkeyandroid.fragments.*;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;

/**
 * @author Dezapp, LLC
 *
 */
public class SettingsActivity extends FragmentActivity implements MMOnMyInfoFragmentItemClickListener,
																  MMOnSocialNetworksFragmentItemClickListener,
																  MMOnMyInterestsFragmentItemClickListener,
																  MMOnFragmentFinishListener {
	private static final String TAG = "SettingsActivtiy: ";
	private FragmentManager fragmentManager;
	private Stack<MMFragment> fragmentStack;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_fragment_container);
		
		fragmentManager = getSupportFragmentManager();
		fragmentStack = new Stack<MMFragment>();
		
		if(findViewById(R.id.llfragmentcontainer) != null) {
			if(savedInstanceState != null) {
				return;
			}
			
			SettingsFragment settingsFragment = new SettingsFragment();
			fragmentManager.beginTransaction().add(R.id.llfragmentcontainer, fragmentStack.push(settingsFragment)).commit();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == MMSDKConstants.REQUEST_CODE_FACEBOOK_SESSION) {
			Session.getActiveSession().onActivityResult(SettingsActivity.this, requestCode, resultCode, data);
		}
	}



	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnMyInfoFragmentItemClickListener#onMyInfoFragmentItemClick()
	 */
	@Override
	public void onMyInfoFragmentItemClick() {
		performTransaction(new MyInfoFragment());
	}
	
	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnSocialNetworksFragmentItemClickListener#onSocialNetworksItemClick()
	 */
	@Override
	public void onSocialNetworksItemClick() {
		performTransaction(new SocialNetworksFragment());
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnMyInterestsFragmentItemClickListener#onMyInterestsFragmentItemClick(org.json.JSONArray, boolean)
	 */
	@Override
	public void onMyInterestsFragmentItemClick(JSONArray myInterests, boolean isTopLevel) {
		MyInterestsFragment myInterestsFragment = new MyInterestsFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_INTERESTS, myInterests.toString());
		data.putBoolean(MMSDKConstants.KEY_INTENT_EXTRA_TOP_LEVEL, isTopLevel);
		myInterestsFragment.setArguments(data);
		performTransaction(myInterestsFragment);
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnFragmentFinishListener#onFragmentFinish()
	 */
	@Override
	public void onFragmentFinish() {		
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentStack.pop();
		fragmentTransaction.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out);
		fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.peek());
		fragmentTransaction.commit();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(TAG, TAG + "Hank got touched");
		return super.onTouchEvent(event);
	}

	/**
	 * Handler when back button is pressed, it will not close and destroy the current {@link Activity} but instead it will remain on the current {@link Activity}
	 */
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {		
		if(fragmentStack.size() > 1) {
			MMFragment mmFragment = fragmentStack.peek();
			mmFragment.onFragmentBackPressed();
		}
		
		moveTaskToBack(true);
		return;
	}
	
	private void performTransaction(MMFragment mmFragment) {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
		fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.push(mmFragment));
		fragmentTransaction.commit();		
	}
}
