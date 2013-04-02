package com.mobmonkey.mobmonkey;

import java.util.Stack;

import com.mobmonkey.mobmonkey.fragments.AssignedRequestsFragment;
import com.mobmonkey.mobmonkey.fragments.InboxFragment;
import com.mobmonkey.mobmonkey.fragments.InboxFragment.OnInboxItemClickListener;
import com.mobmonkey.mobmonkey.fragments.OpenedRequestsFragment;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * @author Dezapp, LLC
 *
 */
public class InboxActivity extends FragmentActivity implements OnInboxItemClickListener {
	FragmentManager fragmentManager;
	Stack<MMFragment> fragmentStack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragmentcontainer);
		
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
	
	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.fragments.InboxFragment.OnInboxItemClickListener#onInboxItemClick(int, java.lang.String)
	 */
	@Override
	public void onInboxItemClick(int position, String requests) {
		MMFragment mmFragment = null;
		switch(position) {
			case 0:
				mmFragment = new OpenedRequestsFragment();
				break;
			case 1:
//				mmFragment = new AnsweredRequestsFragment();
				break;
			case 2:
				mmFragment = new AssignedRequestsFragment();
				break;
			case 3:
//				mmFragment = new NotificationsFragment();
				break;
		}
		
		Bundle data = new Bundle();
		data.putString(MMAPIConstants.KEY_INTENT_EXTRA_INBOX_REQUESTS, requests);
		mmFragment.setArguments(data);
		performTransaction(mmFragment);
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
			fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.peek());
			fragmentTransaction.commit();
		}
		
		moveTaskToBack(true);
		return;
	}
	
	private void performTransaction(MMFragment mmFragment) {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.push(mmFragment));
		fragmentTransaction.commit();		
	}
}