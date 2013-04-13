package com.mobmonkey.mobmonkey.fragments;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.fragments.SearchFragment.OnNoCategoryItemClickListener;
import com.mobmonkey.mobmonkey.utils.MMCategories;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkey.utils.MMProgressDialog;
import com.mobmonkey.mobmonkey.utils.MMSearchResultsCallback;
import com.mobmonkey.mobmonkeyapi.adapters.MMSearchLocationAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationListener;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationManager;

/**
 * @author Dezapp, LLC
 *
 */
public class CategoriesFragment extends MMFragment implements OnItemClickListener {
	private static final String TAG = "CategoriesFragment: ";
	
	private SharedPreferences userPrefs;
	private Location location;
	private double longitudeValue;
	private double latitudeValue;
	
	private TextView tvNavigationBarText;
	private EditText etSearch;
	private ListView lvSubCategories;
	
	private ArrayList<String> subCategories;
	private JSONArray categoriesArray;
	
	private OnSubCategoryItemClickListener subCategoryItemClickListener;
	private OnNoCategoryItemClickListener noCategoryItemClickListener;
	private String searchText;
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
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Context.MODE_PRIVATE);
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		View view = inflater.inflate(R.layout.fragment_categories_screen, container, false);
		tvNavigationBarText = (TextView) view.findViewById(R.id.navtitle);
		etSearch = (EditText) view.findViewById(R.id.etsearch);
		lvSubCategories = (ListView) view.findViewById(R.id.lvsubcategory);
		
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
		if(activity instanceof OnSubCategoryItemClickListener) {
			subCategoryItemClickListener = (OnSubCategoryItemClickListener) activity;
			if(activity instanceof OnNoCategoryItemClickListener) {
				noCategoryItemClickListener = (OnNoCategoryItemClickListener) activity;
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		try {
			JSONObject subCategory = categoriesArray.getJSONObject(position);
			JSONArray subCategoriesArray = new JSONArray(MMCategories.getSubCategoriesWithCategoriId(getActivity(), subCategory.getString(MMAPIConstants.JSON_KEY_CATEGORY_ID)));
			String selectedSubCategory = subCategory.getString(Locale.getDefault().getLanguage());
			
			if(!subCategoriesArray.isNull(0)) {
				subCategoryItemClickListener.onSubCategoryItemClick(subCategoriesArray, selectedSubCategory);
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
		tvNavigationBarText.setText(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE));
		setSearchByText();
		getCurrentLocation();
		
		try {
			categoriesArray = new JSONArray(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_CATEGORY));
			subCategories = new ArrayList<String>();
			
			for(int i = 0; i < categoriesArray.length(); i++) {
				JSONObject category = categoriesArray.getJSONObject(i);
				subCategories.add(category.getString(Locale.getDefault().getLanguage()));
			}
			
	        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.mm_simple_listview_row, R.id.tvlabel, subCategories);
	        
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
	 */
	private void getCurrentLocation() {
		if(location != null) {
			longitudeValue = location.getLongitude();
			latitudeValue = location.getLatitude();
			DecimalFormat decimalFormat = new DecimalFormat("#.######");
			latitudeValue = Double.valueOf(decimalFormat.format(latitudeValue));
			longitudeValue = Double.valueOf(decimalFormat.format(longitudeValue));
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
			noCategoryItemClickListener.onNoCategoryItemClick(true, selectedSubCategory, results);
		}
	}
	
	/**
	 * 
	 */
	private void setSearchByText() {
		etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				Log.d(TAG, TAG + "actionId: " + actionId);
				if(actionId == EditorInfo.IME_ACTION_SEARCH) {
					searchText = etSearch.getText().toString();
					searchByText();
				}
				return true;
			}
		});
		
		if(!MMLocationManager.isGPSEnabled() || MMLocationManager.getGPSLocation(new MMLocationListener()) == null) {
			etSearch.setFocusable(false);
			etSearch.setFocusableInTouchMode(false);
			etSearch.setClickable(false);
		}
	}
	
	/**
	 * 
	 */
	private void searchByText() {
		MMSearchLocationAdapter.searchLocationWithText(
				new SearchCallback(), 
				Double.toString(longitudeValue), 
				Double.toString(latitudeValue), 
				userPrefs.getInt(MMAPIConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMAPIConstants.SEARCH_RADIUS_HALF_MILE), 
				searchText,
				userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
				userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), 
				MMConstants.PARTNER_ID);
		MMProgressDialog.displayDialog(getActivity(), MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_search_for) + MMAPIConstants.DEFAULT_SPACE + searchText + getString(R.string.pd_ellipses));
    	InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
	}
	
	/**
	 * 
	 * @param selectedSubCategory
	 * @param subCategory
	 * @throws JSONException
	 */
	private void searchSubCategory(String selectedSubCategory, JSONObject subCategory) throws JSONException {
		MMProgressDialog.displayDialog(getActivity(), MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_locating) + MMAPIConstants.DEFAULT_SPACE + selectedSubCategory + getString(R.string.pd_ellipses));
		MMSearchLocationAdapter.searchLocationWithCategoryId(
				new MMSearchResultsCallback(getActivity(), selectedSubCategory, new SearchSubCategoryCallback()), 
				Double.toString(longitudeValue), 
				Double.toString(latitudeValue), 
				userPrefs.getInt(MMAPIConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMAPIConstants.SEARCH_RADIUS_HALF_MILE), 
				subCategory.getString(MMAPIConstants.JSON_KEY_CATEGORY_ID),
				userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
				userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), 
				MMConstants.PARTNER_ID);
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	public interface OnSubCategoryItemClickListener {
		public void onSubCategoryItemClick(JSONArray subCategories, String selectedCategory);
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
				
				noCategoryItemClickListener.onNoCategoryItemClick(true, searchText, ((String) obj));
			}
		}
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
				hasResults = true;
				Log.d(TAG, TAG + "hasResults: " + hasResults);
				results = (String) obj;
			}
		}
	}
}
