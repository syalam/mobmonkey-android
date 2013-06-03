package com.mobmonkey.mobmonkeyandroid;

import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.mobmonkey.mobmonkeyandroid.fragments.*;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchLocationsActivity extends SherlockFragmentActivity implements MMOnCreateHotSpotFragmentClickListener,
																		 MMOnNearbyLocationsItemClickListener,
																		 MMOnHistoryFragmentItemClickListener,
																		 MMOnCategoryFragmentItemClickListener,
																		 MMOnMasterLocationNearbyLocationsFragmentItemClickListener,
																		 MMOnCategoryResultsFragmentItemClickListener,
																		 MMOnExistingHotSpotsFragmentItemClickListener,
																		 MMOnExistingHotSpotsFragmentCreateHotSpotClickListener,
																		 MMOnSearchResultsFragmentItemClickListener,
																		 MMOnAddressFragmentItemClickListener,
																		 MMOnAddNotificationsFragmentItemClickListener,
																		 MMOnFragmentMultipleBackListener,
																		 MMOnDeleteHotSpotFragmentFinishListener {
	private static final String TAG = "SearchLocationsActivity: ";
	
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
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnCreateHotSpotFragmentClickListener#onCreateHotSpotClick(org.json.JSONArray)
	 */
	@Override
	public void onCreateHotSpotClick(JSONArray jArr) {
		MasterLocationFragment masterLocationFragment = new MasterLocationFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_NEARBY_LOCATIONS, jArr.toString());
		masterLocationFragment.setArguments(data);
		performTransaction(masterLocationFragment);
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnNearbyLocationsItemClickListener#onNearbyLocationsItemClick(org.json.JSONObject)
	 */
	@Override
	public void onNearbyLocationsItemClick(String location) {
		LocationDetailsFragment locationDetailsFragment = new LocationDetailsFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, location);
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
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnMasterLocationNearbyLocationsFragmentItemClickListener#onMasterLocationNearbyLocationsItemClick(org.json.JSONObject)
	 */
	@Override
	public void onMasterLocationNearbyLocationsItemClick(JSONObject jObj, int requestCode) {
		MMFragment mmFragment = null;
		Bundle data = new Bundle();
		if(jObj.isNull(MMSDKConstants.JSON_KEY_SUB_LOCATIONS)) {
			mmFragment = new NewHotSpotFragment();
			data.putString(MMSDKConstants.KEY_INTENT_EXTRA_HOT_SPOT_LOCATION, jObj.toString());
			data.putInt(MMSDKConstants.REQUEST_CODE, requestCode);
		} else {
			mmFragment = new ExistingHotSpotsFragment();
			data.putString(MMSDKConstants.KEY_INTENT_EXTRA_EXISTING_HOT_SPOTS, jObj.toString());
		}
		
		mmFragment.setArguments(data);
		performTransaction(mmFragment);
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

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnExistingHotSpotsFragmentItemClickListener#onExistingHotSpotsItemClick(org.json.JSONObject)
	 */
	@Override
	public void onExistingHotSpotsItemClick(JSONObject jObj) {
		LocationDetailsFragment locationDetailsFragment = new LocationDetailsFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, jObj.toString());
		locationDetailsFragment.setArguments(data);
		performTransaction(locationDetailsFragment);
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnExistingHotSpotsFragmentCreateHotSpotClickListener#onExistingHotSpotsCreateHotSpotClick(org.json.JSONObject)
	 */
	@Override
	public void onExistingHotSpotsCreateHotSpotClick(JSONObject jObj, int requestCode) {
		NewHotSpotFragment newHotSpotFragment = new NewHotSpotFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_HOT_SPOT_LOCATION, jObj.toString());
		data.putInt(MMSDKConstants.KEY_INTENT_EXTRA_REQUEST_CODE, requestCode);
		newHotSpotFragment.setArguments(data);
		performTransaction(newHotSpotFragment);
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnSearchResultsFragmentItemClickListener#onSearchResultsFragmentItemClick(org.json.JSONObject)
	 */
	@Override
	public void onSearchResultsFragmentItemClick(String locationInfo) {
		LocationDetailsFragment locationDetailsFragment = new LocationDetailsFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, locationInfo);
		locationDetailsFragment.setArguments(data);
		performTransaction(locationDetailsFragment);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnLocationDetailsFragmentItemClickListener#onLocationDetailsFragmentItemClick(int, java.lang.Object)
	 */
	@Override
	public void onAddressFragmentItemClick(JSONObject jObj) {
		LocationDetailsMapFragment locationsDetailsMapFragment = new LocationDetailsMapFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, jObj.toString());
		locationsDetailsMapFragment.setArguments(data);
		performTransaction(locationsDetailsMapFragment);
	}
	
	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnCreateHotSpotFragmentClickListener#onCreateHotSpotClick(org.json.JSONObject)
	 */
	@Override
	public void onCreateHotSpotClick(JSONObject jObj, int requestCode) {
		NewHotSpotFragment newHotSpotFragment = new NewHotSpotFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_HOT_SPOT_LOCATION, jObj.toString());
		data.putInt(MMSDKConstants.KEY_INTENT_EXTRA_REQUEST_CODE, requestCode);
		newHotSpotFragment.setArguments(data);
		performTransaction(newHotSpotFragment);
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnAddNotificationsFragmentItemClickListener#onAddNotificationsFragmentItemClick(org.json.JSONObject)
	 */
	@Override
	public void onAddNotificationsFragmentItemClick(JSONObject jObj) {
		AddNotificationsFragment addNotificationsFragment = new AddNotificationsFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, jObj.toString());
		addNotificationsFragment.setArguments(data);
		performTransaction(addNotificationsFragment);
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnDeleteHotSpotFinishFragmentListener#onFinishDeleteHotSpot(java.lang.String, java.lang.String)
	 */
	@Override
	public void onDeleteHotSpotFinish(String locationId, String providerId) {
		if(fragmentStack.size() > 1) {
			MMFragment mmFragment = fragmentStack.pop();
			
			mmFragment.onFragmentBackPressed();
			
			mmFragment = fragmentStack.peek();
			Bundle data = new Bundle();
			
			
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out);			
			fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.peek());
			fragmentTransaction.commit();
		}
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.listeners.MMOnFragmentMultipleBackListener#onFragmentMultipleBack()
	 */
	@Override
	public void onFragmentMultipleBack() {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		while(fragmentStack.size() > 1) {
			Log.d(TAG, TAG + "fragment: " + fragmentStack.peek());
			fragmentTransaction.remove(fragmentStack.pop());
			if(fragmentStack.peek() instanceof SearchLocationsFragment) {
				fragmentTransaction.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out);
				fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.peek());
				fragmentTransaction.commit();
			}
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
		} else {
			fragmentStack.peek().onFragmentBackPressed();
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
		fragmentTransaction.commitAllowingStateLoss();		
	}
}