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

public class AddLocationCategoryScreen extends Activity implements OnItemClickListener {
	private MMSearchCategoriesItem[] mmSearchCategoriesItems;
	private ArrayAdapter<MMSearchCategoriesItem> arrayAdapter;
	
	protected static final String TAG = "AddLocationCategoryList ";
	private ListView lvCategories;
	
	private JSONArray categories;
	private ArrayList<String> selectedCategories;
	private ArrayList<String> selectedCategoriesIds;
	
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
		
		JSONArray subCategories = MMCategories.getSubCategoriesWithCategoryName(AddLocationCategoryScreen.this, category, categories);
		
		if(subCategories.length() > 1) {
			Intent categoryListIntent = new Intent(AddLocationCategoryScreen.this, AddLocationCategoryScreen.class);
			categoryListIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_SELECTED_CATEGORIES, selectedCategories);
			categoryListIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORY_TITLE, getString(R.string.tv_title_categories));
			categoryListIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORIES, subCategories.toString());
			categoryListIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_TOP_LEVEL, false);
			startActivityForResult(categoryListIntent, MMSDKConstants.REQUEST_CODE_ADD_CATEGORY);
		} else {
			if(!selectedCategories.contains(category)) {
				selectedCategories.add(category);
				selectedCategoriesIds.add(getCategoryId(category));
				mmSearchCategoriesItems[position].setCatIndicatorIconId(android.R.drawable.checkbox_on_background);
			} else {
				selectedCategories.remove(category);
				selectedCategoriesIds.remove(getCategoryId(category));
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
		setResult(Activity.RESULT_CANCELED);
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_right_out);
	}
	
	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btndone:
				saveSelectedCategories();
				break;
		}
	}
	
	private void init() {
		selectedCategories = getIntent().getStringArrayListExtra(MMSDKConstants.KEY_INTENT_EXTRA_SELECTED_CATEGORIES);
		selectedCategoriesIds = getIntent().getStringArrayListExtra(MMSDKConstants.KEY_INTENT_EXTRA_SELECTED_CATEGORIES_IDS);
		lvCategories = (ListView) findViewById(R.id.lvcategories);
		
		try {
			categories = new JSONArray(getIntent().getStringExtra(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORIES));
			setCategoryList();			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
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
			
			JSONArray subCategories = MMCategories.getSubCategoriesWithCategoryName(AddLocationCategoryScreen.this, categoryName, categories);
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
		
		arrayAdapter = new MMSearchCategoriesArrayAdapter(AddLocationCategoryScreen.this, R.layout.listview_row_searchcategory, mmSearchCategoriesItems);
		lvCategories.setAdapter(arrayAdapter);
        lvCategories.setOnItemClickListener(AddLocationCategoryScreen.this);
	}
	
	private String getCategoryId(String category) {
		String categoryId = MMSDKConstants.DEFAULT_STRING_EMPTY;
		
		for(int i = 0; i < categories.length(); i++) {
			try {
				JSONObject jObj = categories.getJSONObject(i);
				if(jObj.getString(Locale.getDefault().getLanguage()).equals(category)) {
					categoryId = jObj.getString(MMSDKConstants.JSON_KEY_CATEGORY_ID);
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return categoryId;
	}
	
	private void saveSelectedCategories() {
		Intent intent = new Intent();
		intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_SELECTED_CATEGORIES, selectedCategories);
		intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_SELECTED_CATEGORIES_IDS, selectedCategoriesIds);
		
		if (getParent() == null) {
		    setResult(Activity.RESULT_OK, intent);
		}
		else {
		    getParent().setResult(Activity.RESULT_OK, intent);
		}
		
		finish();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_right_out);
	}
}