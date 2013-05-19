package com.mobmonkey.mobmonkeyandroid.fragments;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
public class CategoriesFragment extends MMFragment implements OnClickListener,
															  OnItemClickListener {
	private static final String TAG = "CategoriesFragment: ";
	
	private SharedPreferences userPrefs;
	
	private TextView tvNavBarTitle;
	private Button btnAddLoc;
	private ListView lvSubCategories;
	
	private JSONArray categoriesArray;
	
	private MMOnSearchResultsFragmentItemClickListener locationSelectListener;
	private MMOnCategoryFragmentItemClickListener categoryFragmentItemClickListener;
	private MMOnCategoryResultsFragmentItemClickListener categoryResultsFragmentItemClickListener;

	private String searchSubCategory;
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
		lvSubCategories = (ListView) view.findViewById(R.id.lvsubcategory);
		
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
		boolean topLevel = getArguments().getBoolean(MMSDKConstants.KEY_INTENT_EXTRA_TOP_LEVEL);
		
		try {
			categoriesArray = new JSONArray(getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORIES));
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
	        
	        lvSubCategories.setAdapter(arrayAdapter);
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		
		if(!MMLocationManager.isGPSEnabled() || MMLocationManager.getGPSLocation(new MMLocationListener()) == null) {
			lvSubCategories.setEnabled(false);
		} else {
			lvSubCategories.setOnItemClickListener(CategoriesFragment.this);
		}
	}
	
	/**
	 * 
	 * @param selectedSubCategory
	 * @param subCategory
	 * @throws JSONException
	 */
	private void checkCategorySelected(String selectedSubCategory, JSONObject subCategory) throws JSONException {
		if(searchSubCategory == null) {
			searchSubCategory = selectedSubCategory;
			searchSubCategory(selectedSubCategory, subCategory);
		} else if(!searchSubCategory.equals(selectedSubCategory)) {
			searchSubCategory = selectedSubCategory;
			searchSubCategory(selectedSubCategory, subCategory);
		} else if(!hasResults) {
			searchSubCategory = selectedSubCategory;
			searchSubCategory(selectedSubCategory, subCategory);
		} else {
			categoryResultsFragmentItemClickListener.onCategoriesResultsFragmentItemClick(selectedSubCategory, results);
		}
	}
	
	/**
	 * 
	 * @param selectedSubCategory
	 * @param subCategory
	 * @throws JSONException
	 */
	private void searchSubCategory(String selectedSubCategory, JSONObject subCategory) throws JSONException {
		MMSearchLocationAdapter.searchLocationWithCategoryId(new MMSearchResultsCallback(getActivity(), selectedSubCategory, new SearchSubCategoryCallback()),
															 MMLocationManager.getLocationLatitude(),
															 MMLocationManager.getLocationLongitude(),
															 userPrefs.getInt(MMSDKConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMSDKConstants.SEARCH_RADIUS_HALF_MILE),
															 subCategory.getString(MMSDKConstants.JSON_KEY_CATEGORY_ID),
															 MMConstants.PARTNER_ID,
															 userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
															 userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
		MMProgressDialog.displayDialog(getActivity(),
									   MMSDKConstants.DEFAULT_STRING_EMPTY,
									   getString(R.string.pd_locating) + MMSDKConstants.DEFAULT_STRING_SPACE + selectedSubCategory + getString(R.string.pd_ellipses));
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class SearchSubCategoryCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					hasResults = true;
					Log.d(TAG, TAG + "hasResults: " + hasResults);
					results = (String) obj;
				}
			}
		}
	}
}
