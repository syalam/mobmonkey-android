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

import com.mobmonkey.mobmonkeyandroid.fragments.CategoriesFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.LocationDetailsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.LocationDetailsMapFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.SearchFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.SearchResultsFragment;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnCategoryFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnLocationDetailsFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnNoCategoryFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnSearchResultsFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnSubCategoryFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchActivity extends FragmentActivity implements MMOnNoCategoryFragmentItemClickListener, 
																MMOnCategoryFragmentItemClickListener, 
																MMOnSubCategoryFragmentItemClickListener, 
																MMOnSearchResultsFragmentItemClickListener, 
																MMOnLocationDetailsFragmentItemClickListener {
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
			
			SearchFragment searchFragment = new SearchFragment();
			fragmentManager.beginTransaction().add(R.id.llfragmentcontainer, fragmentStack.push(searchFragment)).commit();
		}
	}

	@Override
	public void onNoCategoryFragmentItemClick(boolean showMap, String searchCategory, String results) {
		SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
		Bundle data = new Bundle();
		data.putBoolean(MMSDKConstants.KEY_INTENT_EXTRA_DISPLAY_MAP, showMap);
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, searchCategory);
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS, results);
		searchResultsFragment.setArguments(data);
		performTransaction(searchResultsFragment);
	}

	@Override
	public void onCategoryFragmentItemClick(JSONArray subCategories, String selectedCategory) {
		CategoriesFragment categoriesFragment = new CategoriesFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORY, subCategories.toString());
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, selectedCategory);
		categoriesFragment.setArguments(data);
		performTransaction(categoriesFragment);
	}
	
	@Override
	public void onSubCategoryFragmentItemClick(JSONArray subCategories, String selectedCategory) {
		CategoriesFragment categoriesFragment = new CategoriesFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORY, subCategories.toString());
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, selectedCategory);
		categoriesFragment.setArguments(data);
		performTransaction(categoriesFragment);
	}
	
	@Override
	public void onSearchResultsFragmentItemClick(JSONObject jObj) {
		LocationDetailsFragment locationDetailsFragment = new LocationDetailsFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, jObj.toString());
		locationDetailsFragment.setArguments(data);
		performTransaction(locationDetailsFragment);
	}
	
	@Override
	public void onLocationDetailsFragmentItemClick(int position, Object obj) {
		switch(position) {
		case 0:
			Intent dialerIntent = new Intent(Intent.ACTION_DIAL);
			dialerIntent.setData(Uri.parse("tel:" + ((String) obj)));
			startActivity(dialerIntent);
			break;
		case 1:
			LocationDetailsMapFragment locationDetailsMapFragment = new LocationDetailsMapFragment();
			Bundle data = new Bundle();
			data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, ((String) obj));
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
	
	private void performTransaction(MMFragment mmFragment) {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
		fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.push(mmFragment));
		fragmentTransaction.commit();		
	}
}
