package com.mobmonkey.mobmonkeyandroid;

import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.mobmonkey.mobmonkeyandroid.fragments.AddNotificationsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.CategoriesFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.HistoryFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.LocationDetailsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.LocationDetailsMapFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.SearchLocationsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.SearchResultsFragment;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnCategoryFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnCategoryResultsFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnHistoryFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnLocationDetailsFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnNearbyLocationsItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnSearchResultsFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchLocationsActivity extends FragmentActivity implements MMOnNearbyLocationsItemClickListener,
																MMOnHistoryFragmentItemClickListener,
																MMOnCategoryFragmentItemClickListener,
																MMOnCategoryResultsFragmentItemClickListener,
																MMOnSearchResultsFragmentItemClickListener,
																MMOnLocationDetailsFragmentItemClickListener {
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
			
			SearchLocationsFragment searchLocationsFragment = new SearchLocationsFragment();
			fragmentManager.beginTransaction().add(R.id.llfragmentcontainer, fragmentStack.push(searchLocationsFragment)).commit();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnNearbyLocationsItemClickListener#onNearbyLocationsItemClick(org.json.JSONObject)
	 */
	@Override
	public void onNearbyLocationsItemClick(JSONObject jObj) {
		LocationDetailsFragment locationDetailsFragment = new LocationDetailsFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, jObj.toString());
		locationDetailsFragment.setArguments(data);
		performTransaction(locationDetailsFragment);
	}
	
	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnHistoryFragmentItemClickListener#onHistoryItemClick()
	 */
	@Override
	public void onHistoryItemClick() {
		performTransaction(new HistoryFragment());
	}
	
	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnCategoryFragmentItemClickListener#onCategoryFragmentItemClick(java.lang.String, org.json.JSONArray, boolean)
	 */
	@Override
	public void onCategoryFragmentItemClick(String selectedCategory, JSONArray subCategories, boolean isTopLevel) {
		CategoriesFragment categoriesFragment = new CategoriesFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORY_TITLE, selectedCategory);
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORIES, subCategories.toString());
		data.putBoolean(MMSDKConstants.KEY_INTENT_EXTRA_TOP_LEVEL, isTopLevel);
		categoriesFragment.setArguments(data);
		performTransaction(categoriesFragment);
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnCategoryResultsFragmentItemClickListener#onCategoriesResultsFragmentItemClick(java.lang.String, java.lang.String)
	 */
	@Override
	public void onCategoriesResultsFragmentItemClick(String searchCategory, String results) {
		SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, searchCategory);
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS, results);
		searchResultsFragment.setArguments(data);
		performTransaction(searchResultsFragment);
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnSearchResultsFragmentItemClickListener#onSearchResultsFragmentItemClick(org.json.JSONObject)
	 */
	@Override
	public void onSearchResultsFragmentItemClick(JSONObject jObj) {
		LocationDetailsFragment locationDetailsFragment = new LocationDetailsFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, jObj.toString());
		locationDetailsFragment.setArguments(data);
		performTransaction(locationDetailsFragment);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnLocationDetailsFragmentItemClickListener#onLocationDetailsFragmentItemClick(int, java.lang.Object)
	 */
	@Override
	public void onLocationDetailsFragmentItemClick(int position, Object obj) {
		MMFragment mmFragment;
		Bundle data;
		switch(position) {
			case 0:
				Intent dialerIntent = new Intent(Intent.ACTION_DIAL);
				dialerIntent.setData(Uri.parse("tel:" + ((String) obj)));
				startActivity(dialerIntent);
				break;
			case 1:
				mmFragment = new LocationDetailsMapFragment();
				data = new Bundle();
				data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, ((String) obj));
				mmFragment.setArguments(data);
				performTransaction(mmFragment);
				break;
			case 2:
				mmFragment = new AddNotificationsFragment();
				data = new Bundle();
				data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, ((String) obj));
				mmFragment.setArguments(data);
				performTransaction(mmFragment);
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
	
	/**
	 * 
	 * @param mmFragment
	 */
	private void performTransaction(MMFragment mmFragment) {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
		fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.push(mmFragment));
		fragmentTransaction.commit();		
	}
}