package com.mobmonkey.mobmonkeyandroid.fragments;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobmonkey.mobmonkeyandroid.AddLocationMapScreen;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMSearchCategoriesArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMSearchCategoriesItem;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMCategories;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMSearchLocationAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;

/**
 * @author Dezapp, LLC
 *
 */
public class CategoriesFragment extends MMFragment implements OnClickListener,
															  OnItemClickListener {
	private static final String TAG = "CategoriesFragment: ";
	
	private SharedPreferences userPrefs;
	
	private TextView tvNavBarTitle;
	private Button btnAddLoc;
	private MMExpandedListView elvCategories;
	
	private JSONArray categoriesArray;
	
	private MMOnSearchResultsFragmentItemClickListener locationSelectListener;
	private MMOnCategoryFragmentItemClickListener categoryFragmentItemClickListener;
	private MMOnCategoryResultsFragmentItemClickListener categoryResultsFragmentItemClickListener;

	private String searchedCategory;
	private boolean hasResults = false;
	private String results;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, TAG + "onCreateView");
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		
		View view = inflater.inflate(R.layout.fragment_categories_screen, container, false);
		tvNavBarTitle = (TextView) view.findViewById(R.id.tvnavbartitle);
		btnAddLoc = (Button) view.findViewById(R.id.btnaddloc);
		elvCategories = (MMExpandedListView) view.findViewById(R.id.elvcategories);
		
		btnAddLoc.setOnClickListener(CategoriesFragment.this);
		init();
		
		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MMOnSearchResultsFragmentItemClickListener) {
			locationSelectListener = (MMOnSearchResultsFragmentItemClickListener) activity;
			if(activity instanceof MMOnCategoryFragmentItemClickListener) {
				categoryFragmentItemClickListener = (MMOnCategoryFragmentItemClickListener) activity;
				if(activity instanceof MMOnCategoryResultsFragmentItemClickListener) {
					categoryResultsFragmentItemClickListener = (MMOnCategoryResultsFragmentItemClickListener) activity;
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == MMSDKConstants.REQUEST_CODE_ADD_LOCATION) {
			if(resultCode == Activity.RESULT_OK) {
				locationSelectListener.onSearchResultsFragmentItemClick(data.getStringExtra(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnaddloc:
				startActivityForResult(new Intent(getActivity(), AddLocationMapScreen.class), MMSDKConstants.REQUEST_CODE_ADD_LOCATION);
				break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		try {
			JSONObject subCategory = categoriesArray.getJSONObject(position);
			JSONArray subCategoriesArray = MMCategories.getSubCategoriesWithCategoryName(getActivity(), subCategory.getString(Locale.getDefault().getLanguage()), categoriesArray);
			String selectedSubCategory = subCategory.getString(Locale.getDefault().getLanguage());
			
			if(subCategoriesArray.length() > 1) {
				categoryFragmentItemClickListener.onCategoryFragmentItemClick(selectedSubCategory, subCategoriesArray, false);
			} else {
				checkCategorySelected(selectedSubCategory, subCategory);
			}
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
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
	private void init() {
		tvNavBarTitle.setText(getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORY_TITLE));
		
		try {
			categoriesArray = new JSONArray(getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORIES));
			
			if(categoriesArray.length() > 0) {
				setCategories(getArguments().getBoolean(MMSDKConstants.KEY_INTENT_EXTRA_TOP_LEVEL));
			} else {
				displayUnableToLoadCategoriesAlert();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		
		if(!MMLocationManager.isGPSEnabled() || MMLocationManager.getGPSLocation(new MMLocationListener()) == null) {
			elvCategories.setEnabled(false);
		} else {
			elvCategories.setOnItemClickListener(CategoriesFragment.this);
		}
	}
	
	/**
	 * 
	 * @param topLevel
	 * @throws JSONException
	 */
	private void setCategories(boolean topLevel) throws JSONException {
		MMSearchCategoriesItem[] mmSearchCategoriesItems = new MMSearchCategoriesItem[categoriesArray.length()];
		
		for(int i = 0; i < categoriesArray.length(); i++) {
			JSONObject category = categoriesArray.getJSONObject(i);
			mmSearchCategoriesItems[i] = new MMSearchCategoriesItem();
			mmSearchCategoriesItems[i].setCatName(category.getString(Locale.getDefault().getLanguage()));
			if(topLevel) {
				mmSearchCategoriesItems[i].setCatIconId(MMConstants.topLevelCatIcons[i]);
			} else {
				mmSearchCategoriesItems[i].setCatIconId(MMSDKConstants.DEFAULT_INT_ZERO);
			}
			mmSearchCategoriesItems[i].setCatIndicatorIconId(R.drawable.listview_accessory_indicator);
		}
		
		ArrayAdapter<MMSearchCategoriesItem> arrayAdapter = new MMSearchCategoriesArrayAdapter(getActivity(), R.layout.listview_row_searchcategory, mmSearchCategoriesItems);
		elvCategories.setAdapter(arrayAdapter);
	}
	
	/**
	 * 
	 */
	private void displayUnableToLoadCategoriesAlert() {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View customDialog = inflater.inflate(com.mobmonkey.mobmonkeysdk.R.layout.mmtoast, null);
		ImageView ivToastImage = (ImageView) customDialog.findViewById(R.id.ivtoastimage);
		ivToastImage.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
		
		TextView ivToastText = (TextView) customDialog.findViewById(R.id.tvtoasttext);
		ivToastText.setText(R.string.toast_unable_to_load_categories);
		MMDialog.displayCustomDialog(getActivity(), customDialog);
	}
	
	/**
	 * 
	 * @param selectedCategory
	 * @param subCategory
	 * @throws JSONException
	 */
	private void checkCategorySelected(String selectedCategory, JSONObject subCategory) throws JSONException {
		if(searchedCategory == null) {
			searchedCategory = selectedCategory;
			searchSubCategory(selectedCategory, subCategory);
		} else if(!searchedCategory.equals(selectedCategory)) {
			searchedCategory = selectedCategory;
			searchSubCategory(selectedCategory, subCategory);
		} else if(!hasResults) {
			searchedCategory = selectedCategory;
			searchSubCategory(selectedCategory, subCategory);
		} else {
			categoryResultsFragmentItemClickListener.onCategoriesResultsFragmentItemClick(selectedCategory, results);
		}
	}
	
	/**
	 * 
	 * @param selectedCategory
	 * @param subCategory
	 * @throws JSONException
	 */
	private void searchSubCategory(String selectedCategory, JSONObject subCategory) throws JSONException {
		MMSearchLocationAdapter.searchLocationsWithCategoryIds(new MMSearchLocationsWithCategoryIdsCallback(selectedCategory),
															   MMLocationManager.getLocationLatitude(),
															   MMLocationManager.getLocationLongitude(),
															   userPrefs.getInt(MMSDKConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMSDKConstants.SEARCH_RADIUS_HALF_MILE),
															   subCategory.getString(MMSDKConstants.JSON_KEY_CATEGORY_ID),
															   MMConstants.PARTNER_ID,
															   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
															   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
		MMProgressDialog.displayDialog(getActivity(),
									   MMSDKConstants.DEFAULT_STRING_EMPTY,
									   getString(R.string.pd_locating) + MMSDKConstants.DEFAULT_STRING_SPACE + selectedCategory + getString(R.string.pd_ellipses));
	}
	
	/**
	 * 
	 * @param category
	 */
	private void displayAlertDialog(String category) {
		new AlertDialog.Builder(getActivity())
			.setTitle(category)
			.setMessage(R.string.ad_message_no_locations_found)
			.setCancelable(false)
			.setPositiveButton(R.string.ad_btn_ok, null)
			.show();
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class MMSearchLocationsWithCategoryIdsCallback implements MMCallback {
		private String category;
		
		public MMSearchLocationsWithCategoryIdsCallback(String category) {
			this.category = category;
		}
		
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				Log.d(TAG, TAG + "response: " + ((String) obj));
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), R.string.toast_connection_timed_out, Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONArray searchResults = new JSONArray((String) obj);
						if(searchResults.isNull(0)) {
							hasResults = false;
							results = MMSDKConstants.DEFAULT_STRING_EMPTY;
							displayAlertDialog(category);
						} else {
							JSONArray jArr = MMUtility.filterSubLocations((String) obj);
							hasResults = true;
							results = jArr.toString();
							categoryResultsFragmentItemClickListener.onCategoriesResultsFragmentItemClick(category, jArr.toString());
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
