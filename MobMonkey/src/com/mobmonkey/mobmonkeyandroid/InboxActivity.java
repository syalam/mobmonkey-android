package com.mobmonkey.mobmonkeyandroid;

import java.util.Stack;

import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.fragments.AnsweredRequestsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.AssignedRequestsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.InboxFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.LocationDetailsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.OpenRequestsFragment;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Android {@link Activity} screen displays the Inbox screen.
 * @author Dezapp, LLC
 * 
 */
public class InboxActivity extends FragmentActivity implements MMOnInboxFragmentItemClickListener, OnLocationNameClickFragmentListener {
	private static final String TAG = "InboxActivity"; 
	
	private FragmentManager fragmentManager;
	private Stack<MMFragment> fragmentStack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, TAG + ":onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment_container);
		
		fragmentManager = getSupportFragmentManager();
		fragmentStack = new Stack<MMFragment>();
		
		if(findViewById(R.id.llfragmentcontainer) != null) {
			if(savedInstanceState != null) {
				return;
			}
			
			InboxFragment inboxFragment = new InboxFragment();
			fragmentManager.beginTransaction().add(R.id.llfragmentcontainer, fragmentStack.push(inboxFragment)).commit();
		}
	}
	
	/**
	 * {@link OnItemClickListener} for the {@link ListView} in Inbox screen.
	 */
	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.fragments.InboxFragment.OnInboxItemClickListener#onInboxItemClick(int, java.lang.String)
	 */
	@Override
	public void onInboxFragmentItemClick(int position) {
		MMFragment mmFragment = null;
		switch(position) {
			case 0:
				mmFragment = new OpenRequestsFragment();
				break;
			case 1:
				mmFragment = new AnsweredRequestsFragment();
				break;
			case 2:
				mmFragment = new AssignedRequestsFragment();
				break;
			case 3:
//				mmFragment = new NotificationsFragment();
				break;
		}
		
		//Bundle data = new Bundle();
		//data.putString(MMAPIConstants.KEY_INTENT_EXTRA_INBOX_REQUESTS, requests);
		//mmFragment.setArguments(data);
		performTransaction(mmFragment);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.d(TAG, TAG + ":onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, TAG + ":onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
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
			MMFragment mmFragment = fragmentStack.pop();
			
			mmFragment.onFragmentBackPressed();
			
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out);
			fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.peek());
			fragmentTransaction.commit();
		}
		
		moveTaskToBack(true);
		return;
	}
	
	@Override
	public void locationNameClick(JSONObject obj) {
		Log.d(TAG, obj.toString());
		LocationDetailsFragment locationDetailsFragment = new LocationDetailsFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, obj.toString());
		locationDetailsFragment.setArguments(data);
		performTransaction(locationDetailsFragment);
	}
	
	private void performTransaction(MMFragment mmFragment) {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
		fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.push(mmFragment));
		fragmentTransaction.commit();		
	}
}
