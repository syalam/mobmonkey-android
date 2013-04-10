package com.mobmonkey.mobmonkey;

import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mobmonkey.mobmonkey.fragments.*;
import com.mobmonkey.mobmonkey.fragments.CategoriesFragment.OnSubCategoryItemClickListener;
import com.mobmonkey.mobmonkey.fragments.LocationDetailsFragment.OnLocationDetailsItemClickListener;
import com.mobmonkey.mobmonkey.fragments.SearchFragment.OnCategoryItemClickListener;
import com.mobmonkey.mobmonkey.fragments.SearchFragment.OnNoCategoryItemClickListener;
import com.mobmonkey.mobmonkey.fragments.SearchResultsFragment.OnSearchResultsLocationSelectListener;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchActivity extends FragmentActivity implements OnNoCategoryItemClickListener, 
																OnCategoryItemClickListener, 
																OnSubCategoryItemClickListener, 
																OnSearchResultsLocationSelectListener, 
																OnLocationDetailsItemClickListener {
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
	public void onNoCategoryItemClick(boolean showMap, String searchCategory, String results) {
		SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
		Bundle data = new Bundle();
		data.putBoolean(MMAPIConstants.KEY_INTENT_EXTRA_DISPLAY_MAP, showMap);
		data.putString(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, searchCategory);
		data.putString(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS, results);
		searchResultsFragment.setArguments(data);
		performTransaction(searchResultsFragment);
	}

	@Override
	public void onCategoryItemClick(JSONArray subCategories, String selectedCategory) {
		CategoriesFragment categoriesFragment = new CategoriesFragment();
		Bundle data = new Bundle();
		data.putString(MMAPIConstants.KEY_INTENT_EXTRA_CATEGORY, subCategories.toString());
		data.putString(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, selectedCategory);
		categoriesFragment.setArguments(data);
		performTransaction(categoriesFragment);
	}
	
	@Override
	public void onSubCategoryItemClick(JSONArray subCategories, String selectedCategory) {
		CategoriesFragment categoriesFragment = new CategoriesFragment();
		Bundle data = new Bundle();
		data.putString(MMAPIConstants.KEY_INTENT_EXTRA_CATEGORY, subCategories.toString());
		data.putString(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, selectedCategory);
		categoriesFragment.setArguments(data);
		performTransaction(categoriesFragment);
	}
	
	@Override
	public void onLocationSelect(Object obj) {
		LocationDetailsFragment locationDetailsFragment = new LocationDetailsFragment();
		Bundle data = new Bundle();
		data.putString(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, ((JSONObject) obj).toString());
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
