package com.mobmonkey.mobmonkeyandroid;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMSearchCategoriesArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMSearchCategoriesItem;
import com.mobmonkey.mobmonkeyandroid.utils.MMCategories;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;

public class AddLocationCategoryList extends Activity implements OnItemClickListener {
	private MMSearchCategoriesItem[] mmSearchCategoriesItems;
	private ArrayAdapter<MMSearchCategoriesItem> arrayAdapter;
	
	protected static final String TAG = "AddLocationCategoryList ";
	private ListView lvCategories;
	
	private JSONArray categories;
	private ArrayList<String> selectedCategories;
	
	private int[] topLevelCatIcons = new int[] {R.drawable.cat_icon_coffee_shops,
												R.drawable.cat_icon_schools,
												R.drawable.cat_icon_beaches,
												R.drawable.cat_icon_supermarkets,
												R.drawable.cat_icon_conferences,
												R.drawable.cat_icon_restaurants,
												R.drawable.cat_icon_hotels,
												R.drawable.cat_icon_pubs,
												R.drawable.cat_icon_dog_parks,
												R.drawable.cat_icon_night_clubs,
												R.drawable.cat_icon_stadiums,
												R.drawable.cat_icon_health_clubs,
												R.drawable.cat_icon_cinemas};
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_hold);
		setContentView(R.layout.add_location_category_screen);
		init();
	}

	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View View, int position, long id) {
		String category = ((TextView) View.findViewById(R.id.tvlabel)).getText().toString();
		
		JSONArray subCategories = MMCategories.getSubCategoriesWithCategoryName(AddLocationCategoryList.this, category, categories);
		
		if(subCategories.length() > 1) {
			Intent categoryListIntent = new Intent(AddLocationCategoryList.this, AddLocationCategoryList.class);
			categoryListIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_SELECTED_CATEGORIES, selectedCategories);
			categoryListIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORY_TITLE, getString(R.string.tv_title_categories));
			categoryListIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORY, subCategories.toString());
			categoryListIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_TOP_LEVEL, false);
			startActivityForResult(categoryListIntent, MMSDKConstants.REQUEST_CODE_ADD_CATEGORY);
		} else {
			if(!selectedCategories.contains(category)) {
				selectedCategories.add(category);
				mmSearchCategoriesItems[position].setCatIndicatorIconId(android.R.drawable.checkbox_on_background);
			} else {
				selectedCategories.remove(category);
				mmSearchCategoriesItems[position].setCatIndicatorIconId(android.R.drawable.checkbox_off_background);
			}
			
			arrayAdapter.notifyDataSetChanged();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == MMSDKConstants.REQUEST_CODE_ADD_CATEGORY) {
			if(resultCode == RESULT_OK) {
				selectedCategories = (ArrayList<String>) data.getSerializableExtra(MMSDKConstants.KEY_INTENT_EXTRA_SELECTED_CATEGORIES);
			} else if (resultCode == RESULT_CANCELED) {
				Log.d(TAG, "Cancel");
			}
		}
	}
	
    /* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		try {
			Intent data = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable(MMSDKConstants.KEY_INTENT_EXTRA_SELECTED_CATEGORIES, selectedCategories);
			data.putExtras(bundle);
			
			if (getParent() == null) {
			    setResult(Activity.RESULT_OK, data);
			}
			else {
			    getParent().setResult(Activity.RESULT_OK, data);
			}
		} catch (Exception ex) {
			Log.d(TAG, "onBackPressed: " + ex.toString());
		}
		
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_right_out);
	}
	
	private void init() {
		selectedCategories = getIntent().getStringArrayListExtra(MMSDKConstants.KEY_INTENT_EXTRA_SELECTED_CATEGORIES);
		lvCategories = (ListView) findViewById(R.id.lvcategories);
		
		try {
			categories = new JSONArray(getIntent().getStringExtra(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORY));
			setCategoryList();			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
//	private void refreshList() {
//		if(categories[0].equals(topLevelCategories[0]))
//			getSearchCategoryIcons();
//		else
//			getSubCategoryIcons(categories);
		
//		arrayAdapter = new MMSearchCategoriesArrayAdapter(AddLocationCategoryList.this, 
//										  R.layout.listview_row, 
//										  categoryIcons, 
//										  categories, 
//										  categoryIndicatorIcons, 
//										  android.R.style.TextAppearance_Medium, 
//										  Typeface.DEFAULT_BOLD, 
//										  null);
//		
//		categoriesList.setAdapter(arrayAdapter);
//		arrayAdapter.notifyDataSetChanged();
//	}
	
	private void setCategoryList() throws JSONException {
		boolean topLevel = getIntent().getBooleanExtra(MMSDKConstants.KEY_INTENT_EXTRA_TOP_LEVEL, true);
		mmSearchCategoriesItems = new MMSearchCategoriesItem[categories.length()];
		
		for(int i = 0; i < categories.length(); i++) {
			JSONObject category = categories.getJSONObject(i);
			String categoryName = category.getString(Locale.getDefault().getLanguage());
			
			mmSearchCategoriesItems[i] = new MMSearchCategoriesItem();
			mmSearchCategoriesItems[i].setCatName(categoryName);
			if(topLevel) {
				mmSearchCategoriesItems[i].setCatIconId(topLevelCatIcons[i]);
			} else {
				mmSearchCategoriesItems[i].setCatIconId(MMSDKConstants.DEFAULT_INT_ZERO);
			}
			
			JSONArray subCategories = MMCategories.getSubCategoriesWithCategoryName(AddLocationCategoryList.this, categoryName, categories);
			if(subCategories.length() > 1) {
				mmSearchCategoriesItems[i].setCatIndicatorIconId(R.drawable.listview_accessory_indicator);
			} else {
				if(selectedCategories.contains(categoryName)) {
					mmSearchCategoriesItems[i].setCatIndicatorIconId(android.R.drawable.checkbox_on_background);
				} else {
					mmSearchCategoriesItems[i].setCatIndicatorIconId(android.R.drawable.checkbox_off_background);
				}
			}
		}
		
		arrayAdapter = new MMSearchCategoriesArrayAdapter(AddLocationCategoryList.this, R.layout.listview_row_searchcategory, mmSearchCategoriesItems);
		lvCategories.setAdapter(arrayAdapter);
        lvCategories.setOnItemClickListener(AddLocationCategoryList.this);
	}
	
//	private void getSubCategoryIcons(String[] categories) {
//		categoryIcons = new int[] {};
//		categoryIndicatorIcons = new int[categories.length];
//		for(int i = 0; i < categories.length; i++) {
//			JSONArray newArray = MMCategories.getSubCategoriesWithCategoryName(this, categories[i]);
//			if(!newArray.isNull(0)) {
//				categoryIndicatorIcons[i] = R.drawable.listview_accessory_indicator;
//			} else {
//				try {
//					// check if category is already selected
//					if(selectedCategories.contains(categories[i])) {
//						categoryIndicatorIcons[i] = android.R.drawable.checkbox_on_background;
//					} else {
//						categoryIndicatorIcons[i] = android.R.drawable.checkbox_off_background;
//					}
//				} catch (NullPointerException e) {
//					categoryIndicatorIcons[i]= android.R.drawable.checkbox_off_background;
//				}
//			}
//		}
//	}
}