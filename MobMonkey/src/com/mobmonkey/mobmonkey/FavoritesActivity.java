package com.mobmonkey.mobmonkey;

import java.util.Stack;

import org.json.JSONObject;

import com.mobmonkey.mobmonkey.fragments.*;
import com.mobmonkey.mobmonkey.fragments.FavoritesFragment.OnMMLocationSelectedListener;
import com.mobmonkey.mobmonkey.fragments.FavoritesFragment.OnMapIconClickListener;
import com.mobmonkey.mobmonkey.fragments.LocationDetailsFragment.OnLocationDetailsItemClickListener;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TextView;

/**
 * @author Dezapp, LLC
 *
 */
public class FavoritesActivity extends FragmentActivity implements OnMapIconClickListener, OnMMLocationSelectedListener, OnLocationDetailsItemClickListener {
	private static final String TAG = "FavoritesActivity: ";
	
	FragmentManager fragmentManager;
	Stack<Fragment> fragmentStack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_fragmentcontainer);
		
		fragmentManager = getSupportFragmentManager();
		fragmentStack = new Stack<Fragment>();
		
		if(findViewById(R.id.llfragmentcontainer) != null) {
			if(savedInstanceState != null) {
				return;
			}
			
			FavoritesFragment favoritesFragment = new FavoritesFragment();
			fragmentManager.beginTransaction().add(R.id.llfragmentcontainer, fragmentStack.push(favoritesFragment)).commit();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.fragments.FavoritesFragment.OnMapIconClickListener#onMapIconClicked(java.lang.String)
	 */
	@Override
	public void onMapIconClicked(String favorites) {
		Fragment mmMapsFragment = new MMMapsFragment();
		Bundle arguments = new Bundle();
		arguments.putString("", favorites);
		mmMapsFragment.setArguments(arguments);
		performTransaction(mmMapsFragment);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.fragments.FavoritesFragment.OnMMLocationSelectedListener#onLocationSelected(java.lang.Object)
	 */
	@Override
	public void onLocationSelected(Object obj) {
		Bundle data = new Bundle();
		data.putString(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, ((JSONObject) obj).toString());
		LocationDetailsFragment locationDetailsFragment = new LocationDetailsFragment();
		locationDetailsFragment.setArguments(data);
		performTransaction(locationDetailsFragment);
	}

	@Override
	public void onLocationDetailsItem(int position, Object obj) {
		switch(position) {
			case 0:
				Intent dialerIntent = new Intent(Intent.ACTION_DIAL);
				dialerIntent.setData(Uri.parse("tel:" + ((String) obj)));
				startActivity(dialerIntent);
				break;
			case 1:
				LocationDetailsMapFragment locationDetailsMapFragment = new LocationDetailsMapFragment();
				Bundle data = new Bundle();
				data.putString(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, ((String) obj));
				locationDetailsMapFragment.setArguments(data);
				performTransaction(locationDetailsMapFragment);
				break;
			case 2:
				break;
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
		Log.d(TAG, TAG + "onBackPressed");
		if(fragmentStack.size() > 1) {
			Fragment fragment = fragmentStack.pop();
			
			if(fragment instanceof MMFragment) {			
				((MMFragment) fragment).onFragmentBackPressed();
			} else if(fragment instanceof MMMapsFragment) {
				
			}
			
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.peek());
			fragmentTransaction.commit();
		}
		
		moveTaskToBack(true);
		return;
	}
	
	private void performTransaction(Fragment mmFragment) {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.push(mmFragment));
		fragmentTransaction.commit();		
	}
}
