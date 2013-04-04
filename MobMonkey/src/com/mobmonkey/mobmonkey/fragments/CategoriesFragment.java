package com.mobmonkey.mobmonkey.fragments;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mobmonkey.mobmonkey.CategoryScreen;
import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.utils.MMCategories;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkey.utils.MMSearchResultsCallback;
import com.mobmonkey.mobmonkeyapi.adapters.MMSearchLocationAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationListener;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationManager;

/**
 * @author Dezapp, LLC
 *
 */
public class CategoriesFragment extends MMFragment {
	private static final String TAG = "CategoriesFragment: ";
	
	private SharedPreferences userPrefs;
	private Location location;
	private double longitudeValue;
	private double latitudeValue;
	
	private ProgressDialog progressDialog;
	private ListView lvSubCategories;
	private TextView tvNavigationBarText;
	
	private ArrayList<String> subCategories = new ArrayList<String>();
	private JSONArray categoriesArray;
	
	private OnSubCategoryItemClickListener subCategoryItemClickListener;
	
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
		lvSubCategories = (ListView) view.findViewById(R.id.lvsubcategory);
		tvNavigationBarText = (TextView) view.findViewById(R.id.navtitle);
		tvNavigationBarText.setText(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE));		
		
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
		}
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		
	}
	
	private void init() {
		getCurrentLocation();
		
		try {
			categoriesArray = new JSONArray(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_CATEGORY));
			
			for(int i = 0; i < categoriesArray.length(); i++) {
				JSONObject category = categoriesArray.getJSONObject(i);
				subCategories.add(category.getString(Locale.getDefault().getLanguage()));
			}
			
	        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, subCategories) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);
					TextView eventText = (TextView) view.findViewById(android.R.id.text1);
					eventText.setTypeface(null, Typeface.BOLD);
					return view;
				}
	        };
	        
	        lvSubCategories.setAdapter(arrayAdapter);
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		
		lvSubCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				try {
					JSONObject category = categoriesArray.getJSONObject(position);
					JSONArray subCategoriesArray = new JSONArray(MMCategories.getSubCategoriesWithCategoriId(getActivity(), category.getString(MMAPIConstants.JSON_KEY_CATEGORY_ID)));
					String selectedCategory = category.getString(Locale.getDefault().getLanguage());
					
					if(!subCategoriesArray.isNull(0)) {
						subCategoryItemClickListener.onSubCategoryItemClick(subCategoriesArray, selectedCategory);
					} else {
						progressDialog = ProgressDialog.show(getActivity(), MMAPIConstants.DEFAULT_STRING, "Locating " + selectedCategory, true, false);

						MMSearchLocationAdapter.searchLocationWithText(
								new MMSearchResultsCallback(getActivity(), progressDialog, selectedCategory), 
								Double.toString(longitudeValue), 
								Double.toString(latitudeValue), 
								userPrefs.getInt(MMAPIConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMAPIConstants.SEARCH_RADIUS_HALF_MILE), 
								MMAPIConstants.DEFAULT_STRING,
								category.getString(MMAPIConstants.JSON_KEY_CATEGORY_ID),
								userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
								userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), 
								MMConstants.PARTNER_ID);
					}
				} 
				catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void getCurrentLocation() {
		if(location != null) {
			longitudeValue = location.getLongitude();
			latitudeValue = location.getLatitude();
			DecimalFormat decimalFormat = new DecimalFormat("#.######");
			latitudeValue = Double.valueOf(decimalFormat.format(latitudeValue));
			longitudeValue = Double.valueOf(decimalFormat.format(longitudeValue));
		}
	}
	
	public interface OnSubCategoryItemClickListener {
		public void onSubCategoryItemClick(JSONArray subCategories, String selectedCategory);
	}
}
