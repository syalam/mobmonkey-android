package com.mobmonkey.mobmonkey;

import java.util.Stack;

import org.json.JSONObject;

import com.google.android.gms.maps.model.Marker;
import com.mobmonkey.mobmonkey.fragments.*;
import com.mobmonkey.mobmonkey.fragments.FavoritesFragment.OnMMLocationSelectListener;
import com.mobmonkey.mobmonkey.fragments.FavoritesFragment.OnMapIconClickListener;
import com.mobmonkey.mobmonkey.fragments.LocationDetailsFragment.OnLocationDetailsItemClickListener;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

/**
 * @author Dezapp, LLC
 *
 */
public class FavoritesActivity extends FragmentActivity implements OnMapIconClickListener, OnMMLocationSelectListener, OnLocationDetailsItemClickListener {
	private static final String TAG = "FavoritesActivity: ";
	
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
			
			FavoritesFragment favoritesFragment = new FavoritesFragment();
			fragmentManager.beginTransaction().add(R.id.llfragmentcontainer, 
					fragmentStack.push(favoritesFragment)).commit();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.fragments.FavoritesFragment.OnMapIconClickListener#onMapIconClicked(java.lang.String)
	 */
	@Override
	public void onMapIconClicked(int which) {
		if(which == MMAPIConstants.FAVORITES_FRAGMENT_MAP) {
			fragmentManager.beginTransaction().replace(R.id.llfragmentcontainer, 
					fragmentStack.push(new FavoritesMapFragment())).commit();
		} else if(which == MMAPIConstants.FAVORITES_FRAGMENT_LIST) {		
			fragmentManager.beginTransaction().remove(fragmentStack.pop());
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.peek());
			fragmentTransaction.commit();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.fragments.FavoritesFragment.OnMMLocationSelectedListener#onLocationSelected(java.lang.Object)
	 */
	@Override
	public void onLocationSelect(Object obj) {
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
			if(fragmentStack.peek() instanceof FavoritesMapFragment) {
				// Do nothing?
			} else {
				MMFragment mmFragment = fragmentStack.pop();
				
				mmFragment.onFragmentBackPressed();
				
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out);
				fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.peek());
				fragmentTransaction.commit();
			}
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
