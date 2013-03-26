package com.mobmonkey.mobmonkey;

import java.util.Stack;

import com.mobmonkey.mobmonkey.fragments.*;
import com.mobmonkey.mobmonkey.utils.MMFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * @author Dezapp, LLC
 *
 */
public class SettingsActivity extends FragmentActivity implements SettingsFragment.OnItemClickListener {
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
			
			SettingsFragment settingsFragment = new SettingsFragment();
			fragmentManager.beginTransaction().add(R.id.llfragmentcontainer, fragmentStack.push(settingsFragment)).commit();
		}
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

	@Override
	public void onSettingsItemCLick(int position) {		
		switch(position) {
			case 0:
				performTransaction(new MyInfoFragment());
				break;
			case 1:
				performTransaction(new SocialNetworksFragment());
				break;
			case 2:
				performTransaction(new MyInterestsFragment());
				break;
			case 3:
				startActivity(new Intent(SettingsActivity.this, SubscribeScreen.class));
				break;
		}
	}
	
	private void performTransaction(MMFragment mmFragment) {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.push(mmFragment));
		fragmentTransaction.commit();		
	}
}
