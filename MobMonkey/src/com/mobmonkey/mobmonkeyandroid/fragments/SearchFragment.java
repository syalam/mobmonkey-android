package com.mobmonkey.mobmonkeyandroid.fragments;

import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
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

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.AddLocationMapScreen;
import com.mobmonkey.mobmonkeyandroid.FilterScreen;
import com.mobmonkey.mobmonkeyandroid.utils.MMArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMCategories;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMSearchResultsCallback;
import com.mobmonkey.mobmonkeysdk.adapters.MMSearchLocationAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchFragment extends MMFragment implements OnClickListener {
	private static final String TAG = "SearchFragment: ";
	
	private SharedPreferences userPrefs;
	
	private Location location;
	private double longitudeValue;
	private double latitudeValue;
	
	private String[] topLevelCategories;
	
	private Button btnFilter;
	private Button btnAddLoc;
	private EditText etSearch;
	private MMExpandedListView elvSearchCategory;
	private MMExpandedListView elvSearchNoCategory;
	
	private int[] categoryIcons;
	private int[] categoryIndicatorIcons;
	
	private String searchCategory;
	private String selectedCategory;

	public OnNoCategoryItemClickListener noCategoryItemClickListener;
	public OnCategoryItemClickListener categoryItemClickListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Context.MODE_PRIVATE);
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
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
			getCurrentLocation();
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
		if(activity instanceof OnCategoryItemClickListener) {
			categoryItemClickListener = (OnCategoryItemClickListener) activity;
			if(activity instanceof OnNoCategoryItemClickListener) {
				noCategoryItemClickListener = (OnNoCategoryItemClickListener) activity;
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

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {

	}
	
	private void getCurrentLocation() {
		if(location != null) {
			longitudeValue = location.getLongitude();
			latitudeValue = location.getLatitude();
			DecimalFormat twoDForm = new DecimalFormat("#.######");
			latitudeValue = Double.valueOf(twoDForm.format(latitudeValue));
			longitudeValue = Double.valueOf(twoDForm.format(longitudeValue));
			//latitudeValue = 33.415153;
			//longitudeValue = -111.903949;
		}
	}
	
	private void setSearchByText() {
		etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				Log.d(TAG, TAG + "actionId: " + actionId);
				if(actionId == EditorInfo.IME_ACTION_SEARCH) {
					searchCategory = etSearch.getText().toString();
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
	
	private void setSearchNoCategory() {
		getSearchNoCategoryIcons();
		ArrayAdapter<Object> arrayAdapter = new MMArrayAdapter(getActivity(), R.layout.mm_listview_row, 
				categoryIcons, getResources().getStringArray(R.array.lv_search_nocategory), categoryIndicatorIcons, 
				android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD, null);
		elvSearchNoCategory.setAdapter(arrayAdapter);
		elvSearchNoCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				searchCategory = ((TextView) view.findViewById(R.id.tvlabel)).getText().toString();
				if(position == 0) {				
					searchAllNearbyLocations();
				} else if(position == 1) {
					showHistory();
				}
			}
		});
		
		if(!MMLocationManager.isGPSEnabled() || MMLocationManager.getGPSLocation(new MMLocationListener()) == null) {
			elvSearchNoCategory.setEnabled(false);
		}
	}
	
	private void setSearchCategory() throws JSONException {
		getSearchCategoryIcons();
		ArrayAdapter<Object> arrayAdapter = new MMArrayAdapter(getActivity(), R.layout.mm_listview_row, categoryIcons, 
				getTopLevelCategories(), new int[0], android.R.style.TextAppearance_Medium, 
				Typeface.DEFAULT_BOLD, null);
		elvSearchCategory.setAdapter(arrayAdapter);
		elvSearchCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
				try { //TODO:Change this implementation so that it does not go to subcats
					//String catId = topLevelCategories.getJSONObject(position).getString(MMAPIConstants.JSON_KEY_CATEGORY_ID);
					selectedCategory = topLevelCategories[position];
					JSONArray subCategories = new JSONArray(MMCategories.getSubCategoriesWithCategoryName(getActivity(), selectedCategory));
					
					if(!subCategories.isNull(0)) {
						categoryItemClickListener.onCategoryItemClick(subCategories, selectedCategory);
					} else if(userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES)) {
						JSONObject cats = new JSONObject(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, MMAPIConstants.DEFAULT_STRING));
						MMProgressDialog.displayDialog(getActivity(), MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_locating) + MMAPIConstants.DEFAULT_SPACE + topLevelCategories[position] + getString(R.string.pd_ellipses));
						MMSearchLocationAdapter.searchLocationWithCategoryId(
								new MMSearchResultsCallback(getActivity(), topLevelCategories[position], null), 
								Double.toString(longitudeValue), 
								Double.toString(latitudeValue), 
								userPrefs.getInt(MMAPIConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMAPIConstants.SEARCH_RADIUS_HALF_MILE), 
								cats.getJSONArray(topLevelCategories[position]).getJSONObject(0).getString(MMAPIConstants.JSON_KEY_CATEGORY_ID),
								userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
								userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), 
								MMConstants.PARTNER_ID);
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
	
	private void startAddLocationMapScreen() {
		Intent addLocMapIntent = new Intent(getActivity(), AddLocationMapScreen.class);
		startActivity(addLocMapIntent);
	}
	
	private void searchByText() {
		MMSearchLocationAdapter.searchLocationWithText(
				new SearchCallback(), 
				Double.toString(longitudeValue), 
				Double.toString(latitudeValue), 
				userPrefs.getInt(MMAPIConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMAPIConstants.SEARCH_RADIUS_HALF_MILE), 
				searchCategory,
				userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
				userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), 
				MMConstants.PARTNER_ID);
		MMProgressDialog.displayDialog(getActivity(), MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_search_for) + MMAPIConstants.DEFAULT_SPACE + searchCategory + getString(R.string.pd_ellipses));
    	InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
	}
	
	private void searchAllNearbyLocations() {
		Log.d(TAG, TAG + "search radius: " + userPrefs.getInt(MMAPIConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMAPIConstants.SEARCH_RADIUS_HALF_MILE));
		
		MMSearchLocationAdapter.searchAllNearby(new SearchCallback(), Double.toString(longitudeValue), Double.toString(latitudeValue), 
				userPrefs.getInt(MMAPIConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMAPIConstants.SEARCH_RADIUS_HALF_MILE), userPrefs.getString(MMAPIConstants.KEY_USER, 
				MMAPIConstants.DEFAULT_STRING), userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), MMConstants.PARTNER_ID);
		MMProgressDialog.displayDialog(getActivity(), MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_search_all_nearby));
	}
	
	private void showHistory() {
		noCategoryItemClickListener.onNoCategoryItemClick(false, searchCategory, userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_HISTORY, MMAPIConstants.DEFAULT_STRING));
	}
	
	private String[] getTopLevelCategories() throws JSONException {
		return topLevelCategories;
	}
	
	private void getSearchNoCategoryIcons() {
		categoryIcons = new int[]{R.drawable.cat_icon_show_all_nearby, R.drawable.cat_icon_history};
		categoryIndicatorIcons = new int[]{R.drawable.listview_accessory_indicator, R.drawable.listview_accessory_indicator};				
	}
	
	private void getSearchCategoryIcons() {
		categoryIcons = new int[] {
			R.drawable.cat_icon_coffee_shops, 
			R.drawable.cat_icon_schools, 
			R.drawable.cat_icon_beaches, 
			R.drawable.cat_icon_supermarkets, 
			R.drawable.cat_icon_conferences, 
			R.drawable.cat_icon_restaurants, 
			R.drawable.cat_icon_hotels, 
			R.drawable.cat_icon_night_clubs, 
			R.drawable.cat_icon_pubs, 
			R.drawable.cat_icon_stadiums,
			R.drawable.cat_icon_health_clubs,
			R.drawable.cat_icon_cinemas,
			R.drawable.cat_icon_dog_parks
		};
		categoryIndicatorIcons = new int[] {
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator
		};
	}
	
	public interface OnNoCategoryItemClickListener {
		public void onNoCategoryItemClick(boolean showMap, String searchCategory, String results);
	}
	
	public interface OnCategoryItemClickListener {
		public void onCategoryItemClick(JSONArray subCategories, String selectedCategory);
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
				
				noCategoryItemClickListener.onNoCategoryItemClick(true, searchCategory, ((String) obj));
			}
		}
	}
}
