package com.mobmonkey.mobmonkeyandroid.fragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.AddLocationMapScreen;
import com.mobmonkey.mobmonkeyandroid.FilterScreen;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMSearchCategoriesArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMSearchCategoriesItem;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMCategories;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMSearchResultsCallback;
import com.mobmonkey.mobmonkeysdk.adapters.MMSearchLocationAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchFragment extends MMFragment implements OnClickListener,
														  OnEditorActionListener {
	private static final String TAG = "SearchFragment: ";
	
	private SharedPreferences userPrefs;
	
	private String[] topLevelCategories;
	
	private Button btnFilter;
	private Button btnAddLoc;
	private EditText etSearch;
	private MMExpandedListView elvSearchCategory;
	private MMExpandedListView elvSearchNoCategory;
	
	private String searchCategory;
	private String selectedCategory;

	public MMOnNoCategoryFragmentItemClickListener mmNoCategoryItemClickFragmentListener;
	public MMOnCategoryFragmentItemClickListener mmCategoryItemClickFragmentListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		
		View view = inflater.inflate(R.layout.fragment_search_screen, container, false);
		btnFilter = (Button) view.findViewById(R.id.btnfilter);
		btnAddLoc = (Button) view.findViewById(R.id.btnaddloc);
		etSearch = (EditText) view.findViewById(R.id.etsearch);
		elvSearchNoCategory = (MMExpandedListView) view.findViewById(R.id.elvsearchnocategory);
		elvSearchCategory = (MMExpandedListView) view.findViewById(R.id.elvsearchcategory);
		
		btnFilter.setOnClickListener(SearchFragment.this);
		btnAddLoc.setOnClickListener(SearchFragment.this);
		
		try {
			topLevelCategories = MMCategories.getTopLevelCategories(getActivity().getApplicationContext());
//			getCurrentLocation();
			setSearchByText();
			setSearchNoCategory();
			setSearchCategory();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MMOnCategoryFragmentItemClickListener) {
			mmCategoryItemClickFragmentListener = (MMOnCategoryFragmentItemClickListener) activity;
			if(activity instanceof MMOnNoCategoryFragmentItemClickListener) {
				mmNoCategoryItemClickFragmentListener = (MMOnNoCategoryFragmentItemClickListener) activity;
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnfilter:
				startActivity(new Intent(getActivity(), FilterScreen.class));
				break;
			case R.id.btnaddloc:
				if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
					startAddLocationMapScreen();
				}
				break;
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		Log.d(TAG, TAG + "actionId: " + actionId);
		if(actionId == EditorInfo.IME_ACTION_SEARCH) {
			searchCategory = etSearch.getText().toString();
			searchByText();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {

	}
	
	/**
	 * 
	 */
	private void setSearchByText() {
		etSearch.setOnEditorActionListener(SearchFragment.this);
		
		if(!MMLocationManager.isGPSEnabled() || MMLocationManager.getGPSLocation(new MMLocationListener()) == null) {
			etSearch.setFocusable(false);
			etSearch.setFocusableInTouchMode(false);
			etSearch.setClickable(false);
		}
	}
	
	/**
	 * 
	 */
	private void setSearchNoCategory() {
		MMSearchCategoriesItem[] searchNoCategoryItems = new MMSearchCategoriesItem[2];
		for(int i = 0; i < searchNoCategoryItems.length; i++) {
			searchNoCategoryItems[i] = new MMSearchCategoriesItem();
			searchNoCategoryItems[i].setCatName(getResources().getStringArray(R.array.search_no_category)[i]);
		}
		
		searchNoCategoryItems[0].setCatIconId(R.drawable.cat_icon_show_all_nearby);
		searchNoCategoryItems[1].setCatIconId(R.drawable.cat_icon_history);
		
		ArrayAdapter<MMSearchCategoriesItem> arrayAdapter = new MMSearchCategoriesArrayAdapter(getActivity(), R.layout.listview_row_searchcategory, searchNoCategoryItems);
		elvSearchNoCategory.setAdapter(arrayAdapter);
		elvSearchNoCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				searchCategory = getString(R.string.tv_title_all_nearby);
				if(position == 0) {				
					searchAllNearbyLocations();
				} else if(position == 1) {
					mmNoCategoryItemClickFragmentListener.onNoCategoryFragmentItemClick(position, null, null);
				}
			}
		});
		
		if(!MMLocationManager.isGPSEnabled() || MMLocationManager.getGPSLocation(new MMLocationListener()) == null) {
			elvSearchNoCategory.setEnabled(false);
		}
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void setSearchCategory() throws JSONException {
		MMSearchCategoriesItem[] searchCategoryItems = new MMSearchCategoriesItem[topLevelCategories.length];
		for(int i = 0; i < searchCategoryItems.length; i++) {
			searchCategoryItems[i] = new MMSearchCategoriesItem();
			searchCategoryItems[i].setCatName(topLevelCategories[i]);
		}
		
		searchCategoryItems[0].setCatIconId(R.drawable.cat_icon_coffee_shops);
		searchCategoryItems[1].setCatIconId(R.drawable.cat_icon_schools);
		searchCategoryItems[2].setCatIconId(R.drawable.cat_icon_beaches);
		searchCategoryItems[3].setCatIconId(R.drawable.cat_icon_supermarkets);
		searchCategoryItems[4].setCatIconId(R.drawable.cat_icon_conferences);
		searchCategoryItems[5].setCatIconId(R.drawable.cat_icon_restaurants);
		searchCategoryItems[6].setCatIconId(R.drawable.cat_icon_hotels);
		searchCategoryItems[7].setCatIconId(R.drawable.cat_icon_pubs);
		searchCategoryItems[8].setCatIconId(R.drawable.cat_icon_dog_parks);
		searchCategoryItems[9].setCatIconId(R.drawable.cat_icon_night_clubs);
		searchCategoryItems[10].setCatIconId(R.drawable.cat_icon_stadiums);
		searchCategoryItems[11].setCatIconId(R.drawable.cat_icon_health_clubs);
		searchCategoryItems[12].setCatIconId(R.drawable.cat_icon_cinemas);
		
		ArrayAdapter<MMSearchCategoriesItem> arrayAdapter = new MMSearchCategoriesArrayAdapter(getActivity(), R.layout.listview_row_searchcategory, searchCategoryItems);
		elvSearchCategory.setAdapter(arrayAdapter);
		elvSearchCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
				try {
					selectedCategory = topLevelCategories[position];
					JSONArray subCategories = new JSONArray(MMCategories.getSubCategoriesWithCategoryName(getActivity(), selectedCategory));
					
					if(!subCategories.isNull(0)) {
						mmCategoryItemClickFragmentListener.onCategoryFragmentItemClick(subCategories, selectedCategory);
					} else if(userPrefs.contains(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES)) {
						searchByCategoryId(position);
					}
				} catch (JSONException e) { 
					e.printStackTrace();
				}
			}
		});
		
		if(!MMLocationManager.isGPSEnabled() || MMLocationManager.getGPSLocation(new MMLocationListener()) == null) {
			elvSearchCategory.setEnabled(false);
		}
	}
	
	/**
	 * 
	 */
	private void startAddLocationMapScreen() {
		Intent addLocMapIntent = new Intent(getActivity(), AddLocationMapScreen.class);
		startActivity(addLocMapIntent);
	}
	
	/**
	 * 
	 */
	private void searchByText() {
		MMSearchLocationAdapter.searchLocationWithText(new SearchCallback(), 
													   MMLocationManager.getLocationLatitude(),
													   MMLocationManager.getLocationLongitude(),
													   userPrefs.getInt(MMSDKConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMSDKConstants.SEARCH_RADIUS_HALF_MILE),
													   searchCategory,
													   MMConstants.PARTNER_ID,
													   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
													   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
		MMProgressDialog.displayDialog(getActivity(),
									   MMSDKConstants.DEFAULT_STRING_EMPTY,
									   getString(R.string.pd_search_for) + MMSDKConstants.DEFAULT_STRING_SPACE + searchCategory + getString(R.string.pd_ellipses));
    	InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
	}
	
	/**
	 * 
	 */
	private void searchAllNearbyLocations() {
		MMSearchLocationAdapter.searchAllNearby(new SearchCallback(),
												MMLocationManager.getLocationLatitude(),
												MMLocationManager.getLocationLongitude(),
												userPrefs.getInt(MMSDKConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMSDKConstants.SEARCH_RADIUS_HALF_MILE),
												MMConstants.PARTNER_ID,
												userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
												userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
		MMProgressDialog.displayDialog(getActivity(),
									   MMSDKConstants.DEFAULT_STRING_EMPTY,
									   getString(R.string.pd_search_all_nearby));
	}
	
	/**
	 * 
	 * @param position
	 * @throws JSONException 
	 */
	private void searchByCategoryId(int position) throws JSONException {
		JSONObject cats = new JSONObject(userPrefs.getString(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, MMSDKConstants.DEFAULT_STRING_EMPTY));
		MMSearchLocationAdapter.searchLocationWithCategoryId(new MMSearchResultsCallback(getActivity(), topLevelCategories[position], null), 
															 MMLocationManager.getLocationLatitude(),
															 MMLocationManager.getLocationLongitude(),
															 userPrefs.getInt(MMSDKConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMSDKConstants.SEARCH_RADIUS_HALF_MILE),
															 cats.getJSONArray(topLevelCategories[position]).getJSONObject(0).getString(MMSDKConstants.JSON_KEY_CATEGORY_ID),
															 MMConstants.PARTNER_ID,
															 userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
															 userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
		MMProgressDialog.displayDialog(getActivity(),
									   MMSDKConstants.DEFAULT_STRING_EMPTY,
									   getString(R.string.pd_locating) + MMSDKConstants.DEFAULT_STRING_SPACE + topLevelCategories[position] + getString(R.string.pd_ellipses));
	}
	
    /**
     * Custom {@link MMCallback} specifically for {@link SearchScreen} to be processed after receiving response from MobMonkey server.
     * @author Dezapp, LLC
     *
     */
	private class SearchCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				Log.d(TAG, TAG + "Response: " + ((String) obj));
				
//				try {
//					JSONObject jObj = new JSONObject((String) obj);
//					mmNoCategoryItemClickFragmentListener.onNoCategoryFragmentItemClick(0, searchCategory, jObj.getJSONArray(MMSDKConstants.JSON_KEY_DEFAULT_TEXTS).toString());
					mmNoCategoryItemClickFragmentListener.onNoCategoryFragmentItemClick(0, searchCategory, (String) obj);
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
			}
		}
	}
}
