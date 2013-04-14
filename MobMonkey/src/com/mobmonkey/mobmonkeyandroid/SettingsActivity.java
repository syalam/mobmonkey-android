package com.mobmonkey.mobmonkeyandroid;

import java.util.Stack;

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkeyandroid.fragments.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment.OnFragmentFinishListener;

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
public class SettingsActivity extends FragmentActivity implements SettingsFragment.OnItemClickListener, OnFragmentFinishListener {
	private static final String TAG = "SettingsActivtiy: ";
	private FragmentManager fragmentManager;
	private Stack<MMFragment> fragmentStack;
	
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

	@Override
	public void onSettingsItemClick(int position) {		
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
