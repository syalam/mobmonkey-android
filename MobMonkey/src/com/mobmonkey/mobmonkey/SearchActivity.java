package com.mobmonkey.mobmonkey;

import java.util.Stack;

import org.json.JSONArray;

import com.mobmonkey.mobmonkey.fragments.CategoriesFragment;
import com.mobmonkey.mobmonkey.fragments.SearchFragment;
import com.mobmonkey.mobmonkey.fragments.SearchFragment.OnCategoryItemClickListener;
import com.mobmonkey.mobmonkey.fragments.SearchFragment.OnNoCategoryItemClickListener;
import com.mobmonkey.mobmonkey.fragments.SearchResultsFragment;
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
public class SearchActivity extends FragmentActivity implements OnNoCategoryItemClickListener, OnCategoryItemClickListener {
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
		data.putBoolean(MMAPIConstants.KEY_INTENT_EXTRA_DISPLAY_MAP, true);
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
